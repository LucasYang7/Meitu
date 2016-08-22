package com.xiaozhejun.meitu.ui.activity;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.PhotoViewPagerAdapter;
import com.xiaozhejun.meitu.db.MeituDatabaseHelper;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.util.ShowToast;
import com.xiaozhejun.meitu.util.task.DeleteTempPicturesTask;
import com.xiaozhejun.meitu.util.task.DownloadTask;

import java.io.File;
import java.util.ArrayList;

public class PhotoViewActivity extends AppCompatActivity {

    private ArrayList<MeituPicture> meituPictureArrayList;
    private boolean mIsFavorited;                   // 标记图片是否收藏
    private boolean mIsDownloadPicture;             // 标记图片是否为手机中已经下载好的图片
    public static boolean[] mCanDownloadPicture;      // 判断能否执行下载图片的操作
    private int mPosition;
    private ViewPager mViewPager;
    private TextView mTextView;
    private MenuItem mDownloadMenuItem;
    private MenuItem mFavorMenuItem;
    private MenuItem mUnFavorMenuItem;
    private MeituDatabaseHelper meituDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

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
        mIsDownloadPicture = bundle.getBoolean("isDownload");
        meituPictureArrayList = bundle.getParcelableArrayList("meituPictureList");
        mCanDownloadPicture = new boolean[meituPictureArrayList.size()];
        // 创建数据库
        meituDatabaseHelper = new MeituDatabaseHelper(PhotoViewActivity.this,
                "Meitu.db",null,1);
        mSQLiteDatabase = meituDatabaseHelper.getReadableDatabase();
        // 查看PhotoViewActivity初始化后所展示的第一张图片是否已经被收藏
        mIsFavorited = queryDatabase(meituPictureArrayList.get(mPosition).getPictureUrl());

        mTextView = (TextView)findViewById(R.id.textViewInPhotoViewActivity);
        String pictureDescription = getPictureDescription(mPosition);
        mTextView.setText(pictureDescription);

        mViewPager = (ViewPager)findViewById(R.id.hackyViewPager);
        mViewPager.setAdapter(new PhotoViewPagerAdapter(meituPictureArrayList));
        mViewPager.setCurrentItem(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                //mCanDownloadPicture = false;
                String pictureDescription = getPictureDescription(position);
                String pictureUrl = meituPictureArrayList.get(position).getPictureUrl();
                boolean isFavorited = queryDatabase(pictureUrl);
                mTextView.setText(pictureDescription);
                mPosition = position;
                if(mIsDownloadPicture == false){ //如果不是本地下载图片，则显示toolbar上的收藏菜单按钮
                    changeMenuItemState(isFavorited);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.photo_view,menu);
        mDownloadMenuItem = menu.findItem(R.id.action_download);
        mFavorMenuItem = menu.findItem(R.id.action_favor);
        mUnFavorMenuItem = menu.findItem(R.id.action_unfavor);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mIsDownloadPicture == true){   // 如果是存放在手机中的已经下载好的妹子图片，就只显示分享菜单按钮
            mDownloadMenuItem.setVisible(false);
            mFavorMenuItem.setVisible(false);
            mUnFavorMenuItem.setVisible(false);
        }else{
            changeMenuItemState(mIsFavorited);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_share:
                //ShowToast.showTestShortToast(PhotoViewActivity.this,"分享功能");
                if(mCanDownloadPicture[mPosition] == true){
                    sharePicture(mPosition);
                }else{
                    ShowToast.showShortToast(PhotoViewActivity.this,"正在加载图片，暂时无法分享...");
                }
                return true;

            case R.id.action_download:
                //ShowToast.showTestShortToast(PhotoViewActivity.this,"下载功能");
                if(mCanDownloadPicture[mPosition] == true){
                    downloadPicture(mPosition);
                }else {
                    ShowToast.showShortToast(PhotoViewActivity.this, "正在加载图片，暂时无法下载...");
                }
                return true;

            case R.id.action_favor:
                ShowToast.showShortToast(PhotoViewActivity.this,"收藏成功!");
                //将收藏的图片信息插入到数据库中
                ContentValues values = new ContentValues();
                values.put("pictureUrl",meituPictureArrayList.get(mPosition).getPictureUrl());
                values.put("title",meituPictureArrayList.get(mPosition).getTitle());
                mSQLiteDatabase.insert("Favorites",null,values);
                mIsFavorited = true;
                changeMenuItemState(mIsFavorited);
                return true;

            case R.id.action_unfavor:
                ShowToast.showShortToast(PhotoViewActivity.this,"取消收藏!");
                //将选定的图片从数据库中删除
                mSQLiteDatabase.delete("Favorites","pictureUrl = ?",new String[]{
                        meituPictureArrayList.get(mPosition).getPictureUrl()});
                mIsFavorited = false;
                changeMenuItemState(mIsFavorited);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempPicture();
    }

    /**
     * 改变Menu中的MenuItem的状态
     * */
    public void changeMenuItemState(boolean isFavorited){
        mFavorMenuItem.setVisible(!isFavorited);
        mUnFavorMenuItem.setVisible(isFavorited);
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


    /**
     * 删除共享图片后所产生的临时图片
     * */
    public void deleteTempPicture(){
        // 使用ContentResolver中的delete方法进行删除，使用这种方式在删除照片后能够及时清除相册中的信息
        File tempMeituDir = new File(Environment.getExternalStorageDirectory(),"TempMeitu");
        String tempMeituFolderPath = tempMeituDir.getPath();
        new DeleteTempPicturesTask(PhotoViewActivity.this).execute(new String[]{tempMeituFolderPath});


        // 使用普通的方法删除临时图片
        /*
        File tempMeituDir = new File(Environment.getExternalStorageDirectory(),"TempMeitu");
        if(tempMeituDir.isDirectory()){
            String[] tempPictures = tempMeituDir.list();
            for(String tempPicture:tempPictures){
                new File(tempMeituDir,tempPicture).delete();
            }
        }
        tempMeituDir.delete();
        */

        // use FileUtils.deleteDirectory from the Apache Commons IO library to delete tempMeituDir
        /*
        File tempMeituDir = new File(Environment.getExternalStorageDirectory(),"TempMeitu");
        if(tempMeituDir.isDirectory() && tempMeituDir.exists()){
            try {
                FileUtils.deleteDirectory(tempMeituDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }


    /**
     * 查询某张图片是否已经被收藏
     * */
    public boolean queryDatabase(String pictureUrl){
        Cursor cursor = mSQLiteDatabase.query("Favorites",null,null,null,null,null,null);
        String queryUrl = "";
        boolean isFavorited = false;
        if(cursor.moveToFirst()){
            do{
                queryUrl = cursor.getString(cursor.getColumnIndex("pictureUrl"));
                if(queryUrl.equals(pictureUrl)){
                    isFavorited = true;
                    break;
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        return isFavorited;
    }

}

