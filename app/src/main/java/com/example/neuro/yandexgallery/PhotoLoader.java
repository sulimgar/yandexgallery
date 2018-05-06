package com.example.neuro.yandexgallery;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PhotoLoader extends AsyncTaskLoader<List<String>> {

    public static final String address = "http://neuro.pythonanywhere.com";
    public static final String URL_EXCEPTION_TAG = "URL_EXCEPTION";
    public static final String LAST_LOADED_PHOTO = "last_photo_loaded";
    public static final String GROUP_ID = "group_id";

    private int lastPhoto;
    private int groupID;

    public PhotoLoader(Context context, int lastPhoto, int groupID) {
        super(context);
        this.lastPhoto = lastPhoto;
        this.groupID = groupID;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {//Load photos from server
        List<String> data = new ArrayList<>();
        try {
            URL url = new URL(address + "?" + LAST_LOADED_PHOTO + "=" + lastPhoto + "&" + GROUP_ID + "=" + groupID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            data.addAll(Arrays.asList(line.split("!")));
        } catch (Exception e) {
            Log.e(URL_EXCEPTION_TAG, e.getLocalizedMessage());
        }
        return data;
    }

    @Override
    public void deliverResult(List<String> data) {
        super.deliverResult(data);
    }
}
