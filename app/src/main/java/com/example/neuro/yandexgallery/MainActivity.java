package com.example.neuro.yandexgallery;

import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String SHARED_PREFERENCES = "PHOTOS_CACHE";
    private static final String PHOTO_CACHE = "PHOTO_CACHE";

    private SwipeRefreshLayout refreshLayout;
    private PhotosAdapter adapter;
    private LoaderManager.LoaderCallbacks<List<String>> callback;
    private TextView totalPhotos, loadedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalPhotos = findViewById(R.id.total_photos);
        loadedPhotos = findViewById(R.id.loaded_photos);

        initLayout();

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);

        RecyclerView view = findViewById(R.id.recycle_photos);
        view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        view.setAdapter(adapter);
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter.getItemCount() > 0) {// if we need to load more pictures
                    if (!recyclerView.canScrollVertically(1) && newState == 0) { //reached last loaded photo
                        Bundle bundle = new Bundle();
                        bundle.putInt(PhotoLoader.LAST_LOADED_PHOTO, adapter.getItemCount());
                        getSupportLoaderManager().restartLoader(PhotosCallback.LOAD_PHOTOS, bundle, callback);
                    }
                }
            }
        });
    }

    private void initLayout() {
        adapter = new PhotosAdapter(getApplicationContext(), totalPhotos, loadedPhotos);
        callback = new PhotosCallback(getApplicationContext(), adapter);
        loadTotalPhotos();
        if (!loadCache() && adapter.getData().size() == 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(PhotoLoader.LAST_LOADED_PHOTO, adapter.getItemCount());

            getSupportLoaderManager().initLoader(PhotosCallback.LOAD_PHOTOS, bundle, callback);
        }
    }

    private void loadTotalPhotos() {
        LoaderManager.LoaderCallbacks callbacks = new TotalPhotosCallback(getApplicationContext(), totalPhotos);
        getSupportLoaderManager().initLoader(0, null, callbacks);
    }

    private boolean loadCache() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String cache = preferences.getString(PHOTO_CACHE, "null");
        if (!cache.equals("[]")) {
            Gson gson = new Gson();
            List<Photo> photos = gson.fromJson(cache, new TypeToken<List<Photo>>() {
            }.getType());
            adapter.setData(photos);
            adapter.notifyDataSetChanged();
        }
        return !cache.equals("[]");//return whether photos loaded or not
    }

    @Override
    public void onRefresh() {
        Bundle bundle = new Bundle();
        bundle.putInt(PhotoLoader.LAST_LOADED_PHOTO, adapter.getItemCount());
        getSupportLoaderManager().restartLoader(PhotosCallback.LOAD_PHOTOS, bundle, callback);
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //TODO save only first 40 pictures
        super.onSaveInstanceState(outState);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String cache = gson.toJson(adapter.getData());
        preferences.edit()
                .putString(PHOTO_CACHE, cache)
                .apply();
    }
}
