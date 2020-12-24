package com.project.docshot;

import Adapter.RecycleAdapter;
import Data.ImageFiles;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class gallery extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ImageFiles> imageFiles;
    FloatingActionButton capture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.recyclerView);
        capture=findViewById(R.id.capture);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageFiles = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ImageFiles files = new ImageFiles("File" + (i + 1) + ".pdf", "1 dec 2020", "11:30", "1");
            imageFiles.add(files);
        }
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
}