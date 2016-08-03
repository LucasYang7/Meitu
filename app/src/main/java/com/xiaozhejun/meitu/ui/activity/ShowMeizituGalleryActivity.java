package com.xiaozhejun.meitu.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;

public class ShowMeizituGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meizitu_gallery);

        Bundle bundle = getIntent().getExtras();
        String groupId = bundle.getString("groupId");
        String title = bundle.getString("title");

        TextView textView = (TextView)findViewById(R.id.testTextViewInMeizituGallery);
        textView.setText(title + "\n" + groupId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 为toolbar左边的返回按钮添加事件监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ShowMeizituGalleryActivity.this.finish();
            }
        });
    }

}
