package com.example.neuro.yandexgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ShowPhotoActivity extends AppCompatActivity {

    private ImageView mainPhoto;
    private Photo photo;
    private FloatingActionButton button;
    private String filename = "SaveFile";

    private final String log = "SAVING_ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        mainPhoto = findViewById(R.id.full_photo);
        button = findViewById(R.id.load_button);

        photo = (Photo) getIntent().getSerializableExtra(PhotosAdapter.URL_KEY);
        Glide
                .with(getApplicationContext())
                .load(photo.getHighQuality())
                .into(mainPhoto);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream stream = null;
                try {
                    stream = openFileOutput(filename, Context.MODE_PRIVATE);
                    Bitmap bitmap = ((BitmapDrawable) mainPhoto.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                } catch (IOException e) {
                    Log.e(log, e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        Log.e(log, e.getLocalizedMessage());
                    }
                }
            }
        });
    }
}
