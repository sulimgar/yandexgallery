package com.example.neuro.yandexgallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;


public class ShowPhoto extends AppCompatActivity {

    private ImageView mainPhoto;
    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        mainPhoto = findViewById(R.id.full_photo);

        photo = (Photo) getIntent().getSerializableExtra(PhotosAdapter.URL_KEY);
        Glide
                .with(getApplicationContext())
                .load(photo.getHighQuality())
                .into(mainPhoto);
    }
}
