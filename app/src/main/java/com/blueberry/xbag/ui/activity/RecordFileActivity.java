package com.blueberry.xbag.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;


import com.blueberry.xbag.R;
import com.blueberry.xbag.support.utils.ViewUtils;
import com.blueberry.xbag.ui.fragment.record.RecordFileFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lhq
 *         created at 2015/12/5 15:32
 */
public class RecordFileActivity extends BaseActivity {
    private RecordFileFragment mRecordFileFragment;

    @Bind(R.id.tool_bar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_file);
        ButterKnife.bind(this);

        ViewUtils.setToolbarAsBack(this, mToolbar, "录音记录");

        if (savedInstanceState == null) {
            replaceFragment(R.id.view_holder, mRecordFileFragment = RecordFileFragment.newInstance(1));
        }
    }

    public void replaceFragment(int viewId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }


}
