package com.example.neuro.yandexgallery;

import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

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
        Bundle bundle = new Bundle();
        bundle.putInt(PhotoLoader.LAST_LOADED_PHOTO, adapter.getItemCount());

        getSupportLoaderManager().initLoader(PhotosCallback.LOAD_PHOTOS, bundle, callback);
    }

    @Override
    public void onRefresh() {
        //TODO create bundle with loading args
        Bundle bundle = new Bundle();
        bundle.putInt(PhotoLoader.LAST_LOADED_PHOTO, adapter.getItemCount());
        getSupportLoaderManager().restartLoader(PhotosCallback.LOAD_PHOTOS, bundle, callback);
        refreshLayout.setRefreshing(false);
    }
}
