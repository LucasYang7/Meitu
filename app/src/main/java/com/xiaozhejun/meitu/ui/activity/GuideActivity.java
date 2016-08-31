package com.xiaozhejun.meitu.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.util.Constants;

/**
 * Created by yangzhe on 16-8-31.
 */
public class GuideActivity extends AppIntro2 {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStatusBar(false);     //隐藏标题栏
        // 添加Fragment
        String[] introColors = Constants.INTRO_COLORS;
        String[] introTitles = Constants.INTRO_TITLES;
        int[] introDescriptions = Constants.INTRO_DESCRIPTIONS;
        int[] introImages = Constants.INTRO_IMAGES;
        for(int i = 0;i < Constants.INTRO_TITLES.length;i++){
            addSlide(AppIntroFragment.newInstance(introTitles[i], getString(introDescriptions[i]),
                    introImages[i],Color.parseColor(introColors[i])));
        }
        showSkipButton(false);   // 隐藏"跳过"按钮
        setFlowAnimation();      // 设置动画
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        gotoMainActivity();
    }

    private void showSkipButton(boolean showButton) {
        this.skipButtonEnabled = showButton;
        setButtonState(skipButton, showButton);
    }

    public void gotoMainActivity(){
        Intent intent = new Intent(GuideActivity.this,MainActivity.class);
        startActivity(intent);
        GuideActivity.this.finish();
    }
}
