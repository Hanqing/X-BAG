package com.blueberry.xbag.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blueberry.xbag.MyApplication;
import com.blueberry.xbag.R;
import com.blueberry.xbag.support.scanner.ScannerFragment;
import com.blueberry.xbag.support.scanner.ScannerFragmentListener;
import com.blueberry.xbag.support.service.RecordingService;
import com.blueberry.xbag.support.service.ble.BlinkyService;
import com.blueberry.xbag.support.utils.ViewUtils;
import com.umeng.update.UmengUpdateAgent;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/25.
 */
public class DeviceActivity extends BaseActivity implements ScannerFragmentListener {

    private static final String TAG = "DeviceActivity";

    private static final int REQUEST_ENABLE_BT = 1;

    private boolean mStartRecording = true;

    private BlinkyService.BlinkyBinder mBlinkyDevice;
    private Intent mIntentBlinky;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.content_frame)
    RelativeLayout mContentLayout;

    @Bind(R.id.signal_layout)
    RelativeLayout mSignalLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.container)
    RelativeLayout mParentView;

    @Bind(R.id.fab_search)
    FloatingActionButton mSearchFab;

    @Bind(R.id.warn_btn)
    Button mWarnBtn;

    @Bind(R.id.empty)
    TextView mEmptyView;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBlinkyDevice = (BlinkyService.BlinkyBinder) service;

            if (mBlinkyDevice.isConnected()) {

                if (mBlinkyDevice.isRing()) {
                    mWarnBtn.setText(getString(R.string.warning_cancel));
                } else {
                    mWarnBtn.setText(getString(R.string.warning));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBlinkyDevice = null;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);

        // Ensure that Bluetooth exists
        if (!ensureBleExists())
            finish();

        //显示toolbar并绑定drawerToggle
        ViewUtils.setToolbar(this, mToolbar, true);
        ViewUtils.setDrawerToggle(this, mDrawerLayout, mToolbar);

        initData();

        UmengUpdateAgent.update(this);

        mSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceScanningDialog();
            }
        });
        mWarnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlinkyDevice != null && mBlinkyDevice.isConnected()) {
                    mBlinkyDevice.send(!mBlinkyDevice.isRing());
                } else {
                    showError(getString(R.string.please_connect));
                }
            }
        });
    }

    private void showError(final String error) {
        Snackbar.make(mParentView, error, Snackbar.LENGTH_LONG).show();
    }

    private void showDeviceScanningDialog() {
        final ScannerFragment dialog = ScannerFragment.getInstance(null);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isBleEnabled())
            enableBle();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // empty?
                } else
                    finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRing() {
        mWarnBtn.setText(getString(R.string.warning_cancel));
    }

    public void onRingCancel() {
        mWarnBtn.setText(getString(R.string.warning));
    }

    public void onConnect() {
        showConnected();
    }

    public void onDisconnect() {
        showDisconnected();
    }

    protected DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected NavigationView getNavigationView() {
        return mNavigationView;
    }

    public void onDoubleClick() {
        startRecord(mStartRecording);
        mStartRecording = !mStartRecording;
    }

    public void onOneClick() {
        if (MyApplication.getPageType() != BaseActivity.PAGE_TYPE.CAMERA) {
            showPhoneWarn();
        }
    }

    private void showPhoneWarn() {

    }

    private void startRecord(boolean start) {

        Intent intent = new Intent(this, RecordingService.class);
        if (start) {
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }

            //start RecordingService
            startService(intent);
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } else {
            stopService(intent);
            //allow the screen to turn off again once recording is finished
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showConnected() {
        mContentLayout.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mSignalLayout.setVisibility(View.VISIBLE);
        mSearchFab.setVisibility(View.GONE);
    }

    private void showDisconnected() {
        mContentLayout.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mSignalLayout.setVisibility(View.GONE);
        mSearchFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeviceSelected(final BluetoothDevice device, final String name) {
        mIntentBlinky = new Intent(this, BlinkyService.class);
        mIntentBlinky.putExtra(BlinkyService.EXTRA_DEVICE_ADDRESS, device.getAddress());
        startService(mIntentBlinky);
        boolean flag = bindService(mIntentBlinky, mServiceConnection, 0);
    }

    /**
     * Checks whether the device supports Bluetooth Low Energy communication
     *
     * @return <code>true</code> if BLE is supported, <code>false</code> otherwise
     */
    private boolean ensureBleExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Checks whether the Bluetooth adapter is enabled.
     */
    private boolean isBleEnabled() {
        final BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        return ba != null && ba.isEnabled();
    }

    /**
     * Tries to start Bluetooth adapter.
     */
    private void enableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

}
