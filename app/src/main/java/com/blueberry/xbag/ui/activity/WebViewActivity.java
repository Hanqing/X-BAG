package com.blueberry.xbag.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.blueberry.xbag.R;
import com.blueberry.xbag.support.utils.ViewUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * @author lhq
 *         created at 2015/12/7 4:39
 */
public class WebViewActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_URL = "url";

    private static final String SINA_WEATHER_URL = "http://weather1.sina.cn/?vt=4";

    private String url;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.progressbar)
    ProgressBar progressBar;

    @Bind(R.id.webview)
    WebView webView;

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        //显示toolbar并绑定drawerToggle
        ViewUtils.setToolbar(this, mToolbar, true, "天气");
        ViewUtils.setDrawerToggle(this, mDrawerLayout, mToolbar);

        initData();
        initWebView();
        swipeRefreshLayout.setOnRefreshListener(this);
        int color = getResources().getColor(R.color.cyan_base);
        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        //url = getIntent().getStringExtra(KEY_URL);
        url = SINA_WEATHER_URL;
        webView.loadUrl(url);
    }

    protected DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected NavigationView getNavigationView() {
        return mNavigationView;
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress != 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mToolbar.setTitle(title);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        webView.reload();
        swipeRefreshLayout.setRefreshing(false);
    }
}
