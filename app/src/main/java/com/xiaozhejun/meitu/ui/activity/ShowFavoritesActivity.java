package com.xiaozhejun.meitu.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeituPictureListRecyclerViewAdapter;
import com.xiaozhejun.meitu.db.MeituDatabaseHelper;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView.OnVerticalScrollListener;

import java.util.ArrayList;

public class ShowFavoritesActivity extends AppCompatActivity {

    private MeituRecyclerView mFavoritesRecyclerView;
    private MeituPictureListRecyclerViewAdapter mFavoritesRecyclerViewAdapter;
    private ArrayList<MeituPicture> mFavoritesPictureList = new ArrayList<MeituPicture>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("我的收藏");
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ShowFavoritesActivity.this.finish();
            }
        });

        // 从数据库中查询所收藏的图片信息
        queryFavoritesFromDatabase();

        // 初始化PictureRecyclerView
        setupRecyclerView();

    }

    public void setupRecyclerView(){
        mFavoritesRecyclerView = (MeituRecyclerView)findViewById(R.id.favoritesRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mFavoritesRecyclerViewAdapter = new MeituPictureListRecyclerViewAdapter(
                mFavoritesRecyclerView,false);
        mFavoritesRecyclerViewAdapter.initMeituPictureList(mFavoritesPictureList);
        mFavoritesRecyclerView.setHasFixedSize(true);
        mFavoritesRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mFavoritesRecyclerView.setAdapter(mFavoritesRecyclerViewAdapter);
        mFavoritesRecyclerView.addOnScrollListener(new OnVerticalScrollListener(){

            @Override
            public void onBottom() {

            }
        });

        mFavoritesRecyclerView.setOnItemClickListener(new MeituRecyclerView.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int postion) {

            }
        });

    }

    public void queryFavoritesFromDatabase(){
        // 创建数据库
        MeituDatabaseHelper meituDatabaseHelper = new MeituDatabaseHelper(ShowFavoritesActivity.this,
                "Meitu.db",null,1);
        SQLiteDatabase sqLiteDatabase = meituDatabaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("Favorites",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String pictureUrl = cursor.getString(cursor.getColumnIndex("pictureUrl"));
                MeituPicture meituPicture = new MeituPicture();
                meituPicture.setTitle(title);
                meituPicture.setPictureUrl(pictureUrl);
                mFavoritesPictureList.add(meituPicture);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
}
