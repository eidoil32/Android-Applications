package com.idohayun.manageapplication;

public class FeedEntry {
    String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return
                        ", imageURL=" + imageURL + '\n';
    }
}
