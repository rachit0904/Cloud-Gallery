package com.project.docshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class imagePreview extends AppCompatActivity {
    private ImageView view;
    private ImageButton bckBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        view=findViewById(R.id.imageView);
        bckBtn=findViewById(R.id.bckButton);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(imagePreview.this,gallery.class));
                finish();
            }
        });
        String URL=getIntent().getExtras().get("imageUrl").toString();
        Picasso.
                with(this)
                .load(URL)
                .noFade()
                .placeholder(R.drawable.photos) // can also be a drawable
                .into(view);
    }
}