package com.idohayun.manageapplication;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class ManageCalendar extends AppCompatActivity {

    private static final String TAG = "ManageCalendar";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_calendar);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
