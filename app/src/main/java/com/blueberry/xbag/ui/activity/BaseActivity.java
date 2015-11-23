package com.blueberry.xbag.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blueberry.xbag.MyApplication;
import com.squareup.leakcanary.RefWatcher;

/**
 * 基础类
 * @author lhq
 * created at 2015/10/24 9:47
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
