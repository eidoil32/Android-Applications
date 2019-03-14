package com.idohayun.manageapplication;

import java.util.ArrayList;

public class ImagesURLS {
    private String URL = "http://example.com/file";
    private ArrayList<FeedEntry> images;

    public ImagesURLS() { images = new ArrayList<>();}

    public ArrayList<FeedEntry> getArray() {
        return images;
    }

    public String getURL() {
        return URL;
    }
}
