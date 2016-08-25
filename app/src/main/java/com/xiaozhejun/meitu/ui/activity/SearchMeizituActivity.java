package com.xiaozhejun.meitu.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.ui.fragment.meizitu.MeizituSearchFragment;

public class SearchMeizituActivity extends AppCompatActivity {

    private MeizituSearchFragment meizituSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_meizitu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SearchMeizituActivity.this.finish();
            }
        });

        // 添加MeizituSearchFragment
        meizituSearchFragment = new MeizituSearchFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.searchContentLayout,
                meizituSearchFragment).commit();

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search,menu);
        // 获取Toolbar中的SearchView
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search_meizitu).getActionView();
        // 当前的Activity为searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchMeizitu(query);
        }
    }

    /**
     * 搜索妹子图
     * */
    public void searchMeizitu(String query){
        meizituSearchFragment.setSearchKeyword(query);
        meizituSearchFragment.refreshMeizituGalleryData();
    }

}
