package com.idohayun.mybusiness;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment {
    private static NavigationView navigationView;
    private static String deviceLocale = Locale.getDefault().getLanguage();
    private static TextView pageTitle;

    public HomePage() {
        // Required empty public constructor
    }

    public HomePage(NavigationView navigationView, TextView pageTitle) {
        this.navigationView = navigationView;
        this.pageTitle = pageTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        HomePage_View(view.getContext(),fragmentManager,navigationView);
    }

    public static void HomePage_View(final Context context, final FragmentManager fragmentManager,final NavigationView navigationView) {

        final ImageView appointmentButton, galleryButton;
        final FrameLayout frameLayout = (FrameLayout) ((Activity)(context)).findViewById(R.id.fragment);
        appointmentButton = (ImageView) ((Activity)(context)).findViewById(R.id.btn_new_appointment);
        galleryButton = (ImageView) ((Activity)(context)).findViewById(R.id.btn_gallery);

        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setVisibility(View.VISIBLE);
                pageTitle.setText(R.string.text_order_title);
                OrderView orderView = new OrderView();
                fragmentManager.beginTransaction().add(R.id.fragment,orderView).commit();
                navigationView.getMenu().getItem(1).setChecked(true);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setVisibility(View.VISIBLE);
                pageTitle.setText(R.string.text_gallery_title);
                GalleryView galleryView = new GalleryView();
                fragmentManager.beginTransaction().add(R.id.fragment,galleryView).commit();
                navigationView.getMenu().getItem(1).setChecked(true);
            }
        });
    }


}
