package com.blueberry.xbag.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.blueberry.xbag.MyApplication;
import com.blueberry.xbag.R;
import com.blueberry.xbag.support.listeners.BlinkyUpdateCallback;
import com.blueberry.xbag.support.profile.BleProfileService;
import com.blueberry.xbag.support.service.ble.BlinkyService;
import com.squareup.leakcanary.RefWatcher;

/**
 * 基础类
 *
 * @author lhq
 *         created at 2015/10/24 9:47
 */
public class BaseActivity extends AppCompatActivity implements BlinkyUpdateCallback {

    private BroadcastReceiver mBlinkyUpdateReceiver;

    protected DrawerLayout mDrawerLayout;

    protected NavigationView mNavigationView;

    public enum PAGE_TYPE {
        DEVICE, CAMERA, RECORD, MAP, WEATHER, SETTING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
        initDrawer();
        initBR();
    }

    private void initDrawer() {
        mDrawerLayout = getDrawerLayout();
        mNavigationView = getNavigationView();
        if (mDrawerLayout != null && mNavigationView != null) {
            setUpDrawerContent(mNavigationView);
        }
    }

    protected DrawerLayout getDrawerLayout() {
        return null;
    }

    protected NavigationView getNavigationView() {
        return null;
    }

    private void initBR() {
        mBlinkyUpdateReceiver = getBlinkyUpdateReceiver();
        if (mBlinkyUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mBlinkyUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    protected BroadcastReceiver getBlinkyUpdateReceiver() {
        return new BlinkyUpdateReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlinkyService.BROADCAST_RING_STATE_CHANGED);
        intentFilter.addAction(BlinkyService.BROADCAST_CONNECTION_STATE);
        intentFilter.addAction(BlinkyService.BROADCAST_CLICK);
        return intentFilter;
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_device:
                                MyApplication.setPageType(PAGE_TYPE.DEVICE);
                                startActivity(new Intent(BaseActivity.this, DeviceActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.nav_photo:
                                MyApplication.setPageType(PAGE_TYPE.CAMERA);
                                startActivity(new Intent(BaseActivity.this, CameraActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.nav_map:
                                MyApplication.setPageType(PAGE_TYPE.DEVICE);
                                startActivity(new Intent(BaseActivity.this, MapActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.nav_weather:
                                MyApplication.setPageType(PAGE_TYPE.DEVICE);
                                startActivity(new Intent(BaseActivity.this, WebViewActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.nav_setting:
                                MyApplication.setPageType(PAGE_TYPE.SETTING);
                                startActivity(new Intent(BaseActivity.this, SettingActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                        }

                        return true;
                    }
                });
    }

    class BlinkyUpdateReceiver extends BroadcastReceiver {

        private int pendingCount;

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BlinkyService.BROADCAST_RING_STATE_CHANGED.equals(action)) {
                final boolean flag = intent.getExtras().getBoolean(BlinkyService.EXTRA_DATA);
                if (flag) {
                    onRing();
                } else {
                    onRingCancel();
                }
            } else if (BlinkyService.BROADCAST_CONNECTION_STATE.equals(action)) {
                final int value = intent.getExtras().getInt(BlinkyService.EXTRA_CONNECTION_STATE);
                switch (value) {
                    case BleProfileService.STATE_CONNECTED:
                        onConnect();
                        break;
                    case BleProfileService.STATE_DISCONNECTED:
                        onDisconnect();
                        break;
                }
            } else if (BlinkyService.BROADCAST_CLICK.equals(action)) {
                pendingCount++;
                if (pendingCount <= 1) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (pendingCount > 1) {
                                onDoubleClick();
                            } else {
                                onOneClick();
                            }
                            pendingCount = 0;
                        }
                    }, 500);
                }
                onOneClick();
            }
        }

    }

    public void onRing() {
    }

    public void onRingCancel() {
    }

    public void onConnect() {
    }

    public void onDisconnect() {
    }

    public void onOneClick() {
    }

    public void onDoubleClick() {
    }
}
