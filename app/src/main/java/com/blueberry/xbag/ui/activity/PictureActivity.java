package com.blueberry.xbag.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.blueberry.xbag.R;
import com.blueberry.xbag.support.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PictureActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_PATH = "image_path";
    public static final String EXTRA_IMAGE_TITLE = "image_title";
    public static final String TRANSIT_PIC = "picture";

    @Bind(R.id.picture)
    ImageView mImageView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.appbar_layout)
    AppBarLayout mAppBar;

    PhotoViewAttacher mPhotoViewAttacher;
    String mImagePath, mImageTitle;
    protected boolean mIsHidden = false;

    private void parseIntent() {
        mImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        mImageTitle = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);

        parseIntent();

        // init image view
        ViewCompat.setTransitionName(mImageView, TRANSIT_PIC);
        ViewUtils.setToolbar(this, mToolbar, true, "浏览");

//        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.ARGB_8888)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).considerExifParams(true)
//                .cacheInMemory(false).cacheOnDisk(false).displayer(new FadeInBitmapDisplayer(0)).build();
//        ImageLoader.getInstance().displayImage("file://" + getPhotoPath(), mImageView, options);

        Picasso.with(this).load("file://" + getPhotoPath()).into(mImageView);

        // set up app bar
        setTitle(mImageTitle);

        setupPhotoAttacher();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String getPhotoPath() {
        String PATH_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/X-BAG/";
        String photoPath = "";

        File folder = new File(PATH_DIR);
        if (folder.isDirectory()) {
            File files[] = folder.listFiles();
            if (files.length > 0) {
                photoPath = files[0].getAbsolutePath();
                Log.i("xixi", "photo Path" + photoPath);
            }
        }
        return photoPath;
    }

    protected void hideOrShowToolbar() {
        mAppBar.animate()
                .translationY(mIsHidden ? 0 : -mAppBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();

        mIsHidden = !mIsHidden;
    }

    private void setupPhotoAttacher() {
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                hideOrShowToolbar();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoViewAttacher.cleanup();
        ButterKnife.unbind(this);
    }
}
