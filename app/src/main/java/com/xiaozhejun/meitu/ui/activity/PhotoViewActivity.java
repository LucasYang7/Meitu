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
                ShowToast.showShortToast(PhotoViewActivity.this,"分享功能");

                return true;

            case R.id.action_download:
                //ShowToast.showShortToast(PhotoViewActivity.this,"下载功能");
                String pictureUrl = meituPictureArrayList.get(mPosition).getPictureUrl();
                String title = meituPictureArrayList.get(mPosition).getTitle();
                downloadPicture(pictureUrl,title,mPosition);
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
     * */
    public void sharePicture(int position){
        String title = meituPictureArrayList.get(position).getTitle();
        String pictureUrl = meituPictureArrayList.get(position).getPictureUrl();

    }

    /**
     * 下载图片
     * */
    public void downloadPicture(String pictureUrl,String title,int positon){
        String[] pictureUrls = new String[1];
        pictureUrls[0] = pictureUrl;
        new DownloadTask(this,title,positon).execute(pictureUrls);
    }

}

class DownloadTask extends AsyncTask<String,Void,Uri>{
    private Context mContext;
    private String mTitle;
    private int mPosition;
    private String mExtensions = ".jpg";    // 图片的后缀名

    public DownloadTask(Context context,String title,int position){
        mContext = context;
        mTitle = title;
        mPosition = position;
    }

    @Override
    protected Uri doInBackground(String... url) {
        Uri pictureUri = null;
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(mContext).load(url[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bitmap == null){
            ShowToast.showShortToast(mContext,"无法下载图片...");
        }else{
            File meituDir = new File(Environment.getExternalStorageDirectory(),"Meitu");
            if(meituDir.exists() == false){
                meituDir.mkdir();
            }
            //String pictureName = mTitle.replace('/','_') + "(" + mPosition + ")" +  mExtensions;  //保存到手机中的图片名字
            Resources resources = mContext.getResources();
            String pictureName = String.format(resources.getString(R.string.picture_name),mTitle.replace('/','_'),
                    mPosition+1,mExtensions);
            File picture = new File(meituDir,pictureName);

            try {
                FileOutputStream outputStream = new FileOutputStream(picture);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pictureUri = Uri.fromFile(picture);
            //通知图库更新
            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,pictureUri);
            mContext.sendBroadcast(scannerIntent);
        }
        return pictureUri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        String meituDir=uri.getPath();
        Resources resources = mContext.getResources();
        String downloadMsg = String.format(resources.getString(R.string.picture_has_save_to),meituDir);
        ShowToast.showShortToast(mContext,downloadMsg);
    }
}
