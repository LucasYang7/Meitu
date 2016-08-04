package com.xiaozhejun.meitu.util;

import android.util.Log;

/**
 * Created by yangzhe on 16-8-2.
 */
public class Logcat {
    public static void showLog(String tag, String msg){
        if(Constants.SHOW_LOG == true) {
            Log.e(tag, msg);
        }
    }
}
