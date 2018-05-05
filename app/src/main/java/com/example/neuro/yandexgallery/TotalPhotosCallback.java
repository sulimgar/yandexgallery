package com.example.neuro.yandexgallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.TextView;
import android.widget.Toast;

public class TotalPhotosCallback implements LoaderManager.LoaderCallbacks<String> {
    private Context context;
    private TextView textView;

    public TotalPhotosCallback(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new TotalPhotosLoader(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if(!data.isEmpty()) {
            textView.setText(context.getString(R.string.total_photos, Integer.parseInt(data)));
        } else {
            textView.setText(context.getString(R.string.offline));
            Toast.makeText(context, "Error loading", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
