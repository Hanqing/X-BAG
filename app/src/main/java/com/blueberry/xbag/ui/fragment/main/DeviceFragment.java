package com.blueberry.xbag.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueberry.xbag.R;
import com.blueberry.xbag.ui.fragment.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/23.
 */
public class DeviceFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    protected void lazyLoad() {
    }
}
