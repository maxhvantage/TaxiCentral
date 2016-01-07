package com.taxicentral.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.taxicentral.Tabs.CorporateFragment;
import com.taxicentral.Tabs.MyAccountFragment;
import com.taxicentral.R;

import java.util.ArrayList;
import java.util.List;

public class AccountStatementActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //aa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statement);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyAccountFragment(), "My Account");
        adapter.addFragment(new CorporateFragment(), "Corporate");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_filter:
                Intent intent = new Intent(AccountStatementActivity.this, TripFilterActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

}