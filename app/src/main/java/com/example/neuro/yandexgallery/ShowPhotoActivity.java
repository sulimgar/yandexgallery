package com.example.neuro.yandexgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ShowPhotoActivity extends AppCompatActivity {

    private ImageView mainPhoto;
    private Photo photo;
    private FloatingActionButton button;
    private String filename = "SaveFile.png";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        mainPhoto = findViewById(R.id.full_photo);
        button = findViewById(R.id.load_button);
        progressBar = findViewById(R.id.progressBar);

        photo = (Photo) getIntent().getSerializableExtra(PhotosAdapter.URL_KEY);
        Glide
                .with(getApplicationContext())
                .load(photo.getHighQuality())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getApplicationContext(), "Error loading photo", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mainPhoto);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO manage saving files
                File file = new File(getFilesDir(), filename);

                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(file);
                    Bitmap bitmap = ((BitmapDrawable) mainPhoto.getDrawable()).getBitmap();
                    boolean isSaved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    if (isSaved){
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException ignored) {}
                }
            }
        });
    }
}
