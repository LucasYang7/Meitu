package com.xiaozhejun.meitu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.PhotoViewPagerAdapter;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.ui.widget.ShowToast;
import com.xiaozhejun.meitu.util.DownloadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PhotoViewActivity extends AppCompatActivity {

    private ArrayList<MeituPicture> meituPictureArrayList;
    private int mPosition;
    private ViewPager mViewPager;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewActivity.this.finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        mPosition = bundle.getInt("position");
        meituPictureArrayList = bundle.getParcelableArrayList("meituPictureList");

        mTextView = (TextView)findViewById(R.id.textViewInPhotoViewActivity);
        String pictureDescription = getPictureDescription(mPosition);
        mTextView.setText(pictureDescription);

        mViewPager = (ViewPager)findViewById(R.id.hackyViewPager);
        mViewPager.setAdapter(new PhotoViewPagerAdapter(meituPictureArrayList));
        mViewPager.setCurrentItem(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                String pictureDescription = getPictureDescription(position);
                mTextView.setText(pictureDescription);
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.photo_view,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_share:
                //ShowToast.showShortToast(PhotoViewActivity.this,"分享功能");
                sharePicture(mPosition);
                return true;

            case R.id.action_download:
                //ShowToast.showShortToast(PhotoViewActivity.this,"下载功能");
                downloadPicture(mPosition);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取相册中第position张图片的详细描述信息
     * @param position 图片在相册中的位置
     * */
    public String getPictureDescription(int position){
        int index = position + 1;
        int total = meituPictureArrayList.size();
        String title = meituPictureArrayList.get(position).getTitle();
        //String pictureDescription = index + "/" + total + "\t" + title;
        Resources resources = getResources();
        String pictureDescription = String.format(resources.getString(R.string.photo_view_description),
                index,total,title);
        return pictureDescription;
    }

    /**
     * 分享图片
     * @param position 图片在相册中的位置
     * */
    public void sharePicture(int position){
        String pictureUrl = meituPictureArrayList.get(mPosition).getPictureUrl();
        String title = meituPictureArrayList.get(mPosition).getTitle();
        String[] pictureUrls = new String[1];
        pictureUrls[0] = pictureUrl;
        new DownloadTask(this,"share",title,mPosition).execute(pictureUrls);
    }

    /**
     * 下载图片
     * @param position 图片在相册中的位置
     * */
    public void downloadPicture(int position){
        String pictureUrl = meituPictureArrayList.get(mPosition).getPictureUrl();
        String title = meituPictureArrayList.get(mPosition).getTitle();
        String[] pictureUrls = new String[1];
        pictureUrls[0] = pictureUrl;
        new DownloadTask(this,"download",title,position).execute(pictureUrls);
    }

}

