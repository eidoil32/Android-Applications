package com.idohayun.manageapplication;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Window;

import java.util.Objects;

public class ManageCalendar extends AppCompatActivity {

    private static final String TAG = "ManageCalendar";
    private SectionsPageAdapter mSectionsPageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager viewPager;
        setContentView(R.layout.activity_manage_calendar);

        getWindow().setStatusBarColor(getColor(R.color.status_bar_color));

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    public void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ManageDates(),getString(R.string.manage_dates));
        adapter.addFragment(new ManageAppointments(),getString(R.string.manage_appoint));
        adapter.addFragment(new ManageTreatmentsTypes(),getString(R.string.string_manage_appointment_types));
        viewPager.setAdapter(adapter);
    }
}
