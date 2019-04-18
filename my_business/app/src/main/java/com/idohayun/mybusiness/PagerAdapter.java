package com.idohayun.mybusiness;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String[] tabsTitles;

    PagerAdapter(FragmentManager fm, int NumOfTabs, String[] tabsTitles) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.tabsTitles = tabsTitles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ManageCalendar_AddNewWindow();
            case 1:
                return new ManageTreatmentsTypes();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabsTitles[position];
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}