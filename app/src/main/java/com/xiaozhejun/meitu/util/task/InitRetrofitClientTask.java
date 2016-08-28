package com.xiaozhejun.meitu.util.task;

import android.os.AsyncTask;

import com.xiaozhejun.meitu.network.refresh.InitializeRetrofit;
import com.xiaozhejun.meitu.util.Logcat;

/**
 * Created by yangzhe on 16-8-28.
 */
public class InitRetrofitClientTask extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... params) {
        //InitializeRetrofit.initRetrofitClient();
        InitializeRetrofit.getInstance().initRetrofitClient();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Logcat.showLog("initRetrofitClient","initRetrofitClient finish");
    }
}
