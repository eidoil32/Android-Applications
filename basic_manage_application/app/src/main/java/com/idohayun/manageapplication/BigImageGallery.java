package com.idohayun.manageapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class BigImageGallery extends AppCompatActivity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_gallery);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) url = bundle.getString("IMAGEURL");

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(100);
        webView.loadUrl(url);
    }
}
