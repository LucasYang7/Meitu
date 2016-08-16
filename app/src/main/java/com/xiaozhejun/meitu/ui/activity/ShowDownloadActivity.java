package com.xiaozhejun.meitu.ui.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeituPictureListRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.ui.widget.ShowToast;

import java.io.File;
import java.util.ArrayList;

public class ShowDownloadActivity extends AppCompatActivity {

    private MeituRecyclerView mDownloadRecyclerView;
    private MeituPictureListRecyclerViewAdapter mDownloadRecyclerViewAdapter;
    private ArrayList<MeituPicture> mDownloadPictureList = new ArrayList<MeituPicture>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_download);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("我的下载");
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ShowDownloadActivity.this.finish();
            }
        });

        // 初始化PictureRecyclerView
        setupRecyclerView();
    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();
        mDownloadPictureList.clear();
        File meituDir = new File(Environment.getExternalStorageDirectory(),"Meitu");
        String meituFolderPath = meituDir.getPath();
        new GetDownloadPicturesTask(ShowDownloadActivity.this,mDownloadPictureList
                ,mDownloadRecyclerViewAdapter)
                .execute(new String[]{meituFolderPath});
    }

    public void setupRecyclerView(){
        mDownloadRecyclerView = (MeituRecyclerView)findViewById(R.id.downloadRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mDownloadRecyclerViewAdapter = new MeituPictureListRecyclerViewAdapter(
                mDownloadRecyclerView,false);
        mDownloadRecyclerViewAdapter.initMeituPictureList(mDownloadPictureList);
        mDownloadRecyclerView.setHasFixedSize(true);
        mDownloadRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mDownloadRecyclerView.setAdapter(mDownloadRecyclerViewAdapter);
        mDownloadRecyclerView.addOnScrollListener(new MeituRecyclerView.OnVerticalScrollListener(){

            @Override
            public void onBottom() {
                ShowToast.showLongToast(ShowDownloadActivity.this,"已经没有照片啦");
            }
        });

        mDownloadRecyclerView.setOnItemClickListener(new MeituRecyclerView.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int postion) {
                Bundle bundle = new Bundle();
                bundle.putInt("position",postion);
                bundle.putBoolean("isDownload",true);
                bundle.putParcelableArrayList("meituPictureList",mDownloadPictureList);
                Intent intent = new Intent(ShowDownloadActivity.this,PhotoViewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

}

class GetDownloadPicturesTask extends AsyncTask<String,Void,ArrayList<MeituPicture>>{

    private Context mContext;
    private ArrayList<MeituPicture> mDownloadPictureList;
    private MeituPictureListRecyclerViewAdapter mDownloadRecyclerViewAdapter;

    public GetDownloadPicturesTask(Context context,ArrayList<MeituPicture> downloadPictureList,
                                   MeituPictureListRecyclerViewAdapter downloadRecyclerViewAdapter){
        this.mContext = context;
        this.mDownloadPictureList = downloadPictureList;
        this.mDownloadRecyclerViewAdapter = downloadRecyclerViewAdapter;
    }

    @Override
    protected ArrayList<MeituPicture> doInBackground(String... downloadPicturePath) {
        ArrayList<MeituPicture> downloadPictureList = new ArrayList<MeituPicture>();
        final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final ContentResolver contentResolver = mContext.getContentResolver();
        final String meituFolderPath = "%" + downloadPicturePath[0] + "%"; // 加上%是为了使用sql中的like语句
        Cursor cursor = contentResolver.query(uri,null,MediaStore.Images.Media.DATA + " like ? ",
                new String[]{meituFolderPath},MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(cursor != null){
            while(cursor.moveToNext()){
                String picturePath = "file://" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                MeituPicture meituPicture = new MeituPicture();
                meituPicture.setPictureUrl(picturePath);
                meituPicture.setTitle(title);
                downloadPictureList.add(meituPicture);
            }
        }
        cursor.close();
        return downloadPictureList;
    }

    @Override
    protected void onPostExecute(ArrayList<MeituPicture> meituPictures) {
        super.onPostExecute(meituPictures);
        mDownloadPictureList.addAll(meituPictures);
        mDownloadRecyclerViewAdapter.updateMeituPictureList(meituPictures,true);
    }
}
