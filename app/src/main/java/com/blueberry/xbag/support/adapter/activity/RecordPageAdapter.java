package com.blueberry.xbag.support.adapter.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.blueberry.xbag.support.helper.XbString;
import com.blueberry.xbag.ui.fragment.record.RecordFileFragment;
import com.blueberry.xbag.ui.fragment.record.RecordFragment;

public class RecordPageAdapter extends FragmentStatePagerAdapter {

    public RecordPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RecordFragment.newInstance(position);
            case 1:
                return RecordFileFragment.newInstance(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return XbString.RECORD_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return XbString.RECORD_TITLES[position];
    }
}
