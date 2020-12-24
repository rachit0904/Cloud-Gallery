package com.project.docshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ImageButton galleryBtn,doneBtn,shutterBtn;
    TextureView textureView;
    private static final SparseIntArray ORIENTATION =new SparseIntArray();
    static{
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }
    private String cameraID;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDiamension;
    private ImageReader imageReader;

    private File file;
    private static final int REQUEST_CAMERA_PERMISSION =200;
    private Boolean mFlashSupport;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

        CameraDevice.StateCallback stateCallBack=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice=camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        galleryBtn=findViewById(R.id.galleryBtn);
        doneBtn=findViewById(R.id.doneBtn);
        shutterBtn=findViewById(R.id.captureBtn);
        textureView=findViewById(R.id.cameraBckGrnd);
        doneBtn.setEnabled(false);
        assert textureView !=null;

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,gallery.class));
                finish();
            }
        });

        textureView.setSurfaceTextureListener(textureListner);

        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "saved!", Toast.LENGTH_SHORT).show();
                doneBtn.setEnabled(false);
            }
        });
    }

    private void takePicture() {

        MediaPlayer mp=MediaPlayer.create(MainActivity.this,R.raw.captureaudio);
        mp.start();

        //doneBtn.setEnabled(true);

        if(cameraDevice == null){
            return ;
        }

        CameraManager manager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {

            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes=null;
            if(characteristics != null){
                jpegSizes=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if(jpegSizes!=null && jpegSizes.length>0){
                width=jpegSizes[0].getWidth();
                height=jpegSizes[0].getHeight();
            }

            ImageReader reader=ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation=getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATION.get(rotation));

            //for local storage
            file=new File(Environment.getExternalStorageDirectory()+ "/" +UUID.randomUUID().toString()+".jpg");
            ImageReader.OnImageAvailableListener readerListener=new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image=null;
                    try {
                        image=reader.acquireLatestImage();
                        ByteBuffer buffer=image.getPlanes()[0].getBuffer();
                        byte[] bytes=new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    finally {
                        {
                            if(image!=null){
                                image.close();
                            }
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException{
                    OutputStream outputStream=null;
                    try {
                        outputStream=new FileOutputStream(file);
                        outputStream.write(bytes);
                    }finally {
                        if(outputStream!=null){
                            outputStream.close();
                        }
                    }
                }

            };

            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener=new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this, "Saved"+file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    }catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture=textureView.getSurfaceTexture();
            assert texture!=null;
            texture.setDefaultBufferSize(imageDiamension.getWidth(),imageDiamension.getHeight());
            Surface surface=new Surface(texture);
            captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice==null)
                        return;
                    cameraCaptureSessions=cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
            if(cameraDevice == null){
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
            try{
                cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
    }

    private void openCamera() {
            CameraManager manager=(CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraID=manager.getCameraIdList()[0];
                CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraID);
                StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map!=null;
                imageDiamension=map.getOutputSizes(SurfaceTexture.class)[0];
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)){
                    ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },REQUEST_CAMERA_PERMISSION);
                    return;
                }
              manager.openCamera(cameraID,stateCallBack,null);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
    }

    TextureView.SurfaceTextureListener textureListner=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CAMERA_PERMISSION){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You can't use camera without this permission!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable()){
            openCamera();
        }else{
            textureView.setSurfaceTextureListener(textureListner);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBackgroundThread();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread=null;
            mBackgroundHandler=null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread=new HandlerThread("camera background");
        mBackgroundThread.start();
        mBackgroundHandler=new Handler(mBackgroundThread.getLooper());

    }
}
