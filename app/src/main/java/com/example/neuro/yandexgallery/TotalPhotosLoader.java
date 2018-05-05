package com.example.neuro.yandexgallery;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TotalPhotosLoader extends AsyncTaskLoader<String> {
    public TotalPhotosLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        String res = "";
        try {
            URL url = new URL(PhotoLoader.address + "?get_size=true");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            res = in.readLine();
        } catch (Exception ignored) {
        }
        return res;
    }
}
