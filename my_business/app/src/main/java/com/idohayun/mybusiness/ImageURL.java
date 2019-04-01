package com.idohayun.mybusiness;

import android.util.Log;

public class ImageURL {
    private int id, width, height;
    private String url;

    public ImageURL(String url, int id, int width, int height) {
        this.url = url;
        this.id = id;
        this.height = height;
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url  + " " + id;
    }
}
