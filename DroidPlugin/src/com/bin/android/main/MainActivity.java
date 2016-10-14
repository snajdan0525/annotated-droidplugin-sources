package com.bin.android.main;

import com.bin.android.MainPlugin.R;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


public class MainActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private ViewPager mViewPager;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPager.setAdapter(new FragmentAdapter(this.getSupportFragmentManager()));
    }
}
