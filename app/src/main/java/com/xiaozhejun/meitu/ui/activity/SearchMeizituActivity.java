package com.xiaozhejun.meitu.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.ui.fragment.meizitu.MeizituSearchFragment;
import com.xiaozhejun.meitu.util.Logcat;
import com.xiaozhejun.meitu.util.provider.MeituSuggestionProvider;

public class SearchMeizituActivity extends AppCompatActivity {

    private MeizituSearchFragment meizituSearchFragment;
    private SearchView mSearchView;

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
        Logcat.showLog("SearchMeizituActivity","onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search,menu);
        // 获取Toolbar中的SearchView
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView)menu.findItem(R.id.action_search_meizitu).getActionView();
        // 当前的Activity为searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);                 // 自动展开SearchView
        mSearchView.requestFocusFromTouch();             // 输入框自动获取焦点
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener(){

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor)mSearchView.getSuggestionsAdapter().getItem(position);
                String suggestion = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)); // 获取对应的搜索记录
                mSearchView.setQuery(suggestion,true); // 将获取到的搜索记录输入到搜索框中，并且执行查询操作
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickItemId = item.getItemId();
        if(clickItemId == R.id.action_clear_history){
            showClearSuggestionDataDialog();
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * 处理搜索请求
     * */
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
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MeituSuggestionProvider.AUTHORITY,MeituSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query,null);           // 保存这次搜索所用的关键字
        meizituSearchFragment.setSearchKeyword(query);
        meizituSearchFragment.refreshMeizituGalleryData();
    }

    /**
     * 清除搜索记录
     * */
    public void clearSuggestionData(){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MeituSuggestionProvider.AUTHORITY,MeituSuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    /**
     * 弹出对话框提示用户是否删除搜索记录
     * */
    public void showClearSuggestionDataDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchMeizituActivity.this);
        builder.setTitle("提示")
                .setMessage("确定清除搜索记录？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSuggestionData();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
