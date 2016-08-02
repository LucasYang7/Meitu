package com.xiaozhejun.meitu.ui.widget;

import android.content.Context;
import android.provider.SyncStateContract;
import android.widget.Toast;

import com.xiaozhejun.meitu.util.Constants;

/**
 * Created by yangzhe on 16-7-26.
 */
public class ShowToast {

    public static void showLongToast(Context context,String content){
        if(Constants.SHOW_TOAST == true) {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }

    public static void showShortToast(Context context,String content){
        if(Constants.SHOW_TOAST == true) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }
}
