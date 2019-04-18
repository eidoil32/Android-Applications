package com.idohayun.mybusiness;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;

public class ManagerPanel extends Fragment {
    private static final String TAG = "ManagerPanel";
    PagerAdapter pagerAdapter;
    ViewPager viewPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] tabsTitles = new String[]{"New Window","Manage Treatment Types"};

        pagerAdapter = new PagerAdapter(getChildFragmentManager(),2,tabsTitles);
        viewPager = view.findViewById(R.id.view_pager_container);
        viewPager.setAdapter(pagerAdapter);

        MainActivity.changeTitlePage(getString(R.string.text_manage_panel_title));

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_calendar, container, false);
    }
}
