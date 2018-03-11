package com.xiaozhejun.meitu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeituPictureListRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.util.ShowToast;
import com.xiaozhejun.meitu.util.task.GetDownloadPicturesTask;

import java.io.File;
import java.util.ArrayList;

public class ShowDownloadActivity extends BaseActivity {

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
        mDownloadRecyclerViewAdapter.initMeituPictureList(null);
        mDownloadRecyclerView.setHasFixedSize(true);
        mDownloadRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mDownloadRecyclerView.setAdapter(mDownloadRecyclerViewAdapter);
        mDownloadRecyclerView.addOnScrollListener(new MeituRecyclerView.OnVerticalScrollListener(){

            @Override
            public void onBottom() {
                ShowToast.showShortToast(ShowDownloadActivity.this,"已经没有照片啦");
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
