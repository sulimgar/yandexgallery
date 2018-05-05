package com.example.neuro.yandexgallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotosCallback implements LoaderManager.LoaderCallbacks<List<String>> {
    public static final int LOAD_PHOTOS = -15;
    public static final int RESPONSE_OK = 0;
    public static final int RESPONSE_BAD = 1;

    public static final String SHARED_PREFERENCES = "ResponsePreferences";
    public static final String SERVER_RESPONSE = "ServerResponse";

    private Context context;
    private PhotosAdapter adapter;
    private SharedPreferences preferences;
    private SwipeRefreshLayout refreshLayout;

    public PhotosCallback(Context context, PhotosAdapter adapter, SwipeRefreshLayout refreshLayout) {
        this.context = context;
        this.adapter = adapter;
        preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.refreshLayout = refreshLayout;
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new PhotoLoader(context, args.getInt(PhotoLoader.LAST_LOADED_PHOTO, 0));
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<String>> loader, List<String> data) {
        refreshLayout.setRefreshing(false);
        if (data.size() > 0) {//Check if loaded any
            List<Photo> photos = new ArrayList<>();
            for (String photoData : data.subList(1, data.size())) {//skip first empty string and create Photo obj for each
                Photo p = new Photo();
                p.setUrls(Arrays.asList(photoData.split(",")));
                photos.add(p);
            }
            adapter.appendData(photos);
            adapter.notifyDataSetChanged();
            writeResponse(RESPONSE_OK);
        } else {
            Toast.makeText(context, "No photos loaded", Toast.LENGTH_SHORT).show();
            writeResponse(RESPONSE_BAD);
        }
    }

    private void writeResponse(int response) {
        preferences.edit().putInt(SERVER_RESPONSE, response).apply();
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<List<String>> loader) {

    }
}
