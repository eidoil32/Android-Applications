package com.idohayun.mybusiness;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class HomePage extends Fragment {
    private static TextView userState;
    private static final String TAG = "HomePage";

    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        view.setBackgroundColor(view.getResources().getColor(R.color.colorBackground,null));
        MainActivity.changeTitlePage(" ");
        Log.d(TAG, "onCreateView: homepage started!");

        final ImageView appointmentButton, galleryButton;
        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment);

        appointmentButton = (ImageView) view.findViewById(R.id.btn_new_appointment);
        galleryButton = (ImageView) view.findViewById(R.id.btn_gallery);
        userState = (TextView) view.findViewById(R.id.homepage_user_details);


        String deviceLocale = Locale.getDefault().getLanguage();
        if(deviceLocale.equals("iw")) {
            appointmentButton.setImageDrawable(getResources().getDrawable(R.drawable.button_appointment_he,null));
            galleryButton.setImageDrawable(getResources().getDrawable(R.drawable.button_gallery_empty_he,null));
        }


        baseUSER user = new baseUSER();
        user.getUserDetails(view);
        if(user.isExist()) {
            userState.setText(view.getResources().getString(R.string.welcome_user_exist_text,user.getName()));
        } else {
            userState.setText(view.getResources().getString(R.string.welcome_user_doesnt_exist));
        }

        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderView orderView = new OrderView();
                getFragmentManager().beginTransaction().replace(R.id.fragment,orderView).addToBackStack("OrderView").commit();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryView galleryView = new GalleryView();
                getFragmentManager().beginTransaction().replace(R.id.fragment,galleryView).addToBackStack("GalleryView").commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
