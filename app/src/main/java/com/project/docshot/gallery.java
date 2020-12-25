package com.project.docshot;

import Adapter.RecycleAdapter;
import Data.ImageFiles;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class gallery extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ImageFiles> imageFiles;
    FloatingActionButton capture;
    FirebaseStorage storage;
    StorageReference listRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        storage= FirebaseStorage.getInstance();
        listRef= storage.getReference().child("images/");

        recyclerView = findViewById(R.id.recyclerView);
        capture=findViewById(R.id.capture);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageFiles = new ArrayList<>();
        imageFiles=getdata();
        adapter = new RecycleAdapter(this,imageFiles);
        recyclerView.setAdapter(adapter);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(gallery.this,MainActivity.class));
                finish();
            }
        });
    }

    private List<ImageFiles> getdata() {
        List<ImageFiles> arrayList=new ArrayList<>();
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        final int[] idno = {0};
                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    ImageFiles files=new ImageFiles(uri.toString(),"","","","");
                                    arrayList.add(files);
                                    adapter.notifyDataSetChanged();
                                    idno[0]++;
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(gallery.this, "Something Wents Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
        return arrayList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}