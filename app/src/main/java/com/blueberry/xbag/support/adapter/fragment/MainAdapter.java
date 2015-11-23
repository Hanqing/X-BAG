package com.blueberry.xbag.support.adapter.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.blueberry.xbag.support.helper.XbString;
import com.blueberry.xbag.ui.fragment.main.DeviceFragment;


/**
 * 主页Fragment适配器
 *
 * @author lhq
 *         created at 2015/10/24 10:13
 */
public class MainAdapter extends FragmentStatePagerAdapter {

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DeviceFragment();
            case 1:
                return new DeviceFragment();
            case 2:
                return new DeviceFragment();
            case 3:
                return new DeviceFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return XbString.MAIN_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return XbString.MAIN_TITLES[position];
    }
}
