package com.xiaozhejun.meitu.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.task.InitRetrofitClientTask;

import org.w3c.dom.Text;

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

        // 设置字体的样式
        TextView textView = (TextView)findViewById(R.id.textViewInSplash);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/android_7.ttf");
        textView.setTypeface(typeface);

        SharedPreferences settings = getSharedPreferences(Constants.MEITU_PREFS_NAME,0);
        boolean isFirstStart = settings.getBoolean("isFirstStart",true);
        if(isFirstStart){
            saveSettings();
            enterGuideActivity();
        }else{
            // 初始化Retrofit客户端
            new InitRetrofitClientTask().execute();
            // 延迟2秒后进入MainActivity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHomeActivity();
                }
            },2000);
        }
    }

    public void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    public void enterGuideActivity(){
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    public void saveSettings(){
        SharedPreferences settings = getSharedPreferences(Constants.MEITU_PREFS_NAME,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isFirstStart",false);
        editor.commit();
    }
}
