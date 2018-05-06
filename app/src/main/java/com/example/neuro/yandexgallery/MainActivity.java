package com.example.neuro.yandexgallery;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String SHARED_PREFERENCES = "PHOTOS_CACHE";
    private static final String PHOTO_CACHE = "PHOTO_CACHE";
    private static final String GROUP_ID = "GROUP_ID";
    private int groupID;

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

        groupID = -1;

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);

        initLayout();

        RecyclerView view = findViewById(R.id.recycle_photos);
        view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        view.setAdapter(adapter);
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter.getItemCount() > 0) {// if we need to load more pictures
                    if (!recyclerView.canScrollVertically(1) && newState == 0) { //reached last loaded photo
                        loadPhotos();
                    }
                    if(totalPhotos.getText().toString().equals(getApplicationContext().getString(R.string.offline)))
                        loadTotalPhotos();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private void initLayout() {
        adapter = new PhotosAdapter(getApplicationContext(), totalPhotos, loadedPhotos);
        callback = new PhotosCallback(getApplicationContext(), adapter, refreshLayout);
        if (!loadCache() && adapter.getData().size() == 0) {//if we have no photos in adapter and cache, load them
            loadPhotos();
        }
        if (totalPhotos.getText().toString().equals(getApplicationContext().getString(R.string.offline))) {
            loadTotalPhotos();//Trying to get total amount of photos in the album
        }
    }

    private void loadTotalPhotos() {
        if (groupID != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(PhotoLoader.GROUP_ID, groupID);
            LoaderManager.LoaderCallbacks callbacks = new TotalPhotosCallback(getApplicationContext(), totalPhotos);
            getSupportLoaderManager().restartLoader(0, bundle, callbacks);
        }
    }

    private void loadPhotos(){
        if (groupID != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(PhotoLoader.LAST_LOADED_PHOTO, adapter.getItemCount());
            bundle.putInt(PhotoLoader.GROUP_ID, groupID);

            getSupportLoaderManager().restartLoader(PhotosCallback.LOAD_PHOTOS, bundle, callback);
        } else {
            Toast.makeText(getApplicationContext(), "Input group ID in settings", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean loadCache() {//See if any photos were cached
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String cache = preferences.getString(PHOTO_CACHE, "null");
        groupID = preferences.getInt(GROUP_ID, -1);
        if (!(cache.equals("[]") || cache.equals("null"))) {
            Gson gson = new Gson();
            List<Photo> photos = gson.fromJson(cache, new TypeToken<List<Photo>>() {
            }.getType());
            adapter.setData(photos);
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;//return whether photos loaded or not
    }

    @Override
    public void onRefresh() {//on refresh load more photos
        loadPhotos();
        if(totalPhotos.getText().toString().equals(getApplicationContext().getString(R.string.offline)))
            loadTotalPhotos();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {//Try to save loaded photos
        super.onSaveInstanceState(outState);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        List<Photo> data = adapter.getData();
        if (data.size() > 40) {
            data = data.subList(0, 40);
        }
        String cache = gson.toJson(data);
        preferences.edit()
                .putString(PHOTO_CACHE, cache)
                .apply();
        preferences.edit().putInt(GROUP_ID, groupID).apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache: {
                clearCache();
                return true;
            }
            case R.id.clear_photos: {
                clearPhotos();
                return true;
            }
            case R.id.new_group: {
                changeGroup();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void changeGroup() {//Set new group id to load photos from
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(getApplicationContext(), R.layout.dialog_layout, null);
        final EditText id = v.findViewById(R.id.group_id);
        builder.setMessage(getApplicationContext().getString(R.string.enter_id))
                .setView(v)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.clearData();

                        groupID = Integer.parseInt(id.getText().toString());
                        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                        preferences.edit().putInt(GROUP_ID, groupID).apply();
                        totalPhotos.setText(getApplicationContext().getString(R.string.offline));
                        loadTotalPhotos();
                        loadPhotos();
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearPhotos() { //delete all saved photos
        File dir = getFilesDir();
        String savedPhotos[] = dir.list();
        for (String photo : savedPhotos) {
            File file = new File(dir, photo);
            file.delete();
        }
        Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT).show();
    }

    private void clearCache() { //delete all cached information
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        preferences.edit().clear().apply();
        Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT).show();
    }

}
