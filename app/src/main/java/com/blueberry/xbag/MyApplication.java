package com.blueberry.xbag;

import android.app.Application;
import android.content.Context;

import com.blueberry.xbag.ui.activity.BaseActivity;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

/**
 * Application类
 * @author lhq
 * created at 2015/10/22 21:05
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    public static volatile Context applicationContext;

    private static BaseActivity.PAGE_TYPE mPageType = BaseActivity.PAGE_TYPE.DEVICE;

    public static MyApplication getsInstance() {
        return sInstance;
    }

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();

        return application.refWatcher;
    }

    public static BaseActivity.PAGE_TYPE getPageType() {
        return mPageType;
    }

    public static void setPageType(BaseActivity.PAGE_TYPE mPageType) {
        MyApplication.mPageType = mPageType;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //由android系统帮你实例化的
        sInstance = this;
        applicationContext = getApplicationContext();
        refWatcher = LeakCanary.install(sInstance);

        ButterKnife.setDebug(BuildConfig.DEBUG);
    }

}
