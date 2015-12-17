package com.blueberry.xbag.ui.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.blueberry.xbag.R;
import com.blueberry.xbag.support.utils.ViewUtils;
import com.blueberry.xbag.ui.fragment.setting.SettingFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lhq
 *         created at 2015/12/5 15:32
 */
public class SettingActivity extends BaseActivity {
    private SettingFragment mSettingFragment;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        ViewUtils.setToolbarAsBack(this, mToolbar, getString(R.string.setting_title));

        if (savedInstanceState == null) {
            replaceFragment(R.id.setting_frame, mSettingFragment = SettingFragment.newInatance());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSettingFragment.onActivityResult(requestCode, resultCode, data);
    }


    public void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }


}
