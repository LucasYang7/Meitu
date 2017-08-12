package com.xiaozhejun.meitu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiaozhejun.meitu.util.ShowToast;

/**
 * Created by yangzhe on 16-8-15.
 */
public class MeituDatabaseHelper extends SQLiteOpenHelper {

    /**
     * 创建Favorites表的Sql语句
     */
    public static final String CREATE_MEITU_FAVORITES = "create table Favorites(" +
            "pictureUrl text primary key," +
            "title text," +
            "referer text," +
            "addTime text)";

    private Context mContext;

    public MeituDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEITU_FAVORITES);
        ShowToast.showTestShortToast(mContext, "Create Favorites table succeeded!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
