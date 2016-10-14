package com.bin.android.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


/** 
 * @author dhunter
 * @time   2016-7-6 上午11:28:31 
 */
public class FragmentAdapter extends FragmentPagerAdapter {
	
	private static final String[] TITLES = {"已安装","未安装"};
	private static final String TAG = FragmentAdapter.class.getSimpleName();
	public FragmentAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
        if (position == 0) {
            return new InstalledFragment(); // 已安装页
        } else {
            return new StoreFragment(); // 想要安装页
        }
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return TITLES.length;
	}

	@Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}

