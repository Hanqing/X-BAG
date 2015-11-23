package com.blueberry.xbag.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.blueberry.xbag.R;
import com.blueberry.xbag.support.adapter.activity.RecordPageAdapter;
import com.blueberry.xbag.support.utils.ViewUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/23.
 */
public class RecordActivity extends BaseActivity {
    @Bind(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.fragment_record_tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.fragment_record_viewpager)
    ViewPager mViewPager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        setUpDrawerContent(mNavigationView);

        //显示toolbar并绑定drawerToggle
        ViewUtils.setToolbar(this, mToolbar, true);
        ViewUtils.setDrawerToggle(this, mDrawerLayout, mToolbar);

        if (mTabLayout != null && mViewPager != null) {
            //设置tab模式,可以滚动
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            //mTabLayout.setTabTextColors();
            RecordPageAdapter adapter = new RecordPageAdapter(getSupportFragmentManager());
            //给ViewPager设置适配器
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(2);
            //关联TabLayout和ViewPager
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_record:
                                startActivity(new Intent(RecordActivity.this, CameraActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.nav_home:
                                startActivity(new Intent(RecordActivity.this, MainActivity.class));
                                overridePendingTransition(0, 0);
                                break;
                        }

                        return true;
                    }
                });
    }
}
