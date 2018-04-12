package com.example.neuro.yandexgallery;

import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private PhotosAdapter adapter;
    private LoaderManager.LoaderCallbacks<List<String>> callback;
    private TextView totalPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalPhotos = findViewById(R.id.total_photos);

        initLayout();

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);

        RecyclerView view = findViewById(R.id.recycle_photos);
        view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        view.setAdapter(adapter);
    }

    private void initLayout() {
        adapter = new PhotosAdapter(getApplicationContext(), totalPhotos);
        callback = new PhotosCallback(getApplicationContext(), adapter);

        getSupportLoaderManager().initLoader(PhotosCallback.LOAD_PHOTOS, null, callback);
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().initLoader(PhotosCallback.LOAD_PHOTOS, null, callback);
        refreshLayout.setRefreshing(false);
    }
}
