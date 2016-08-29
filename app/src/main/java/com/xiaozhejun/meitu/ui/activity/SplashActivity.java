package com.xiaozhejun.meitu.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.util.task.InitRetrofitClientTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 加载布局
        setContentView(R.layout.activity_splash);
        // 初始化Retrofit客户端
        new InitRetrofitClientTask().execute();
        // 延迟3秒后进入MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHomeActivity();
            }
        },3000);
    }

    public void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}
