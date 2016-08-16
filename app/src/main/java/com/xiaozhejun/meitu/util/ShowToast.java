package com.xiaozhejun.meitu.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yangzhe on 16-7-26.
 */
public class ShowToast {

    public static void showTestLongToast(Context context, String content){
        if(Constants.SHOW_TEST_TOAST == true) {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }

    public static void showTestShortToast(Context context, String content){
        if(Constants.SHOW_TEST_TOAST == true) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showLongToast(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
