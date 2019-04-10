package com.idohayun.manageapplication;

import org.jetbrains.annotations.NotNull;

public class FeedEntry {
    private String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @NotNull
    @Override
    public String toString() {
        return
                        ", imageURL=" + imageURL + '\n';
    }
}
