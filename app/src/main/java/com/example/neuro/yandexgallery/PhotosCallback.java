package com.example.neuro.yandexgallery;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.widget.Toast;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotosCallback implements LoaderManager.LoaderCallbacks<List<String>> {
    public static final int LOAD_PHOTOS = -15;

    private Context context;
    private PhotosAdapter adapter;

    public PhotosCallback(Context context, PhotosAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new PhotoLoader(context);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<String>> loader, List<String> data) {
        if(data.size() > 0) {
            adapter.setTotal(Integer.parseInt(data.get(0)));
            List<Photo> photos = new ArrayList<>();
            for (String photoData : data.subList(1, data.size())) {
                Photo p = new Photo();
                p.setUrls(Arrays.asList(photoData.split(",")));
                photos.add(p);
            }
            adapter.setData(photos);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(context, "No photos loaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<List<String>> loader) {

    }
}
