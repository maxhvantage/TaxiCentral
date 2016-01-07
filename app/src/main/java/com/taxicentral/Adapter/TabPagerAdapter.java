package com.taxicentral.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.taxicentral.Tabs.CorporateFragment;
import com.taxicentral.Tabs.MyAccountFragment;

/**
 * Created by MMFA-MANISH on 11/7/2015.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Fragement for Android Tab
                return new MyAccountFragment();
            case 1:
                return new CorporateFragment();

        }
        return null;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 2;
    }

}