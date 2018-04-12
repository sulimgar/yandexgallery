package com.example.neuro.yandexgallery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable{
    List<String> urls;

    Photo() {
        urls = new ArrayList<>();
    }

    public void addURL(String url) {
        urls.add(url);
    }

    public String getLowQuality() {
        return urls.get(1);
    }

    public String getHighQuality() {
        return urls.get(urls.size() - 1);
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }
}
