package com.xiaozhejun.meitu.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.ui.fragment.doubanmeinv.DoubanMeinvTabFragment;
import com.xiaozhejun.meitu.ui.fragment.gankmeizi.GankMeiziFragment;
import com.xiaozhejun.meitu.ui.fragment.huabanmeinv.HuabanMeinvFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.MeizituTabFragment;
import com.xiaozhejun.meitu.util.ShowToast;
import com.xiaozhejun.meitu.util.task.InitRetrofitClientTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private long exitTime = 0;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.meizitu_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);  //这句话的作用是使navigationView中的icon显示原有的颜色
        navigationView.setNavigationItemSelectedListener(this);

        // 使用妹子图网站作为初始化页面
        MeizituTabFragment meizituTabFragment = new MeizituTabFragment();
        replaceFragment(meizituTabFragment);

        // 初始化Retrofit客户端
        // new InitRetrofitClientTask().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //ShowToast.showShortToast(MainActivity.this,"搜索功能还未实现 ╮(╯▽╰)╭");
            gotoOtherActivity(SearchMeizituActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean isCloseDrawer = false;
        switch (id){
            case R.id.nav_meizitu:
                ShowToast.showTestShortToast(MainActivity.this,"妹子图");
                getSupportActionBar().setTitle(R.string.meizitu_title);
                changeSearchMenuItemState(true);
                MeizituTabFragment meizituTabFragment = new MeizituTabFragment();
                replaceFragment(meizituTabFragment);
                isCloseDrawer = true;
                break;

            case R.id.nav_douban_meinv:
                ShowToast.showTestShortToast(MainActivity.this,"豆瓣美女");
                getSupportActionBar().setTitle(R.string.douban_meinv_title);
                changeSearchMenuItemState(false);
                DoubanMeinvTabFragment doubanMeinvFragment = new DoubanMeinvTabFragment();
                replaceFragment(doubanMeinvFragment);
                isCloseDrawer = true;
                break;

            case R.id.nav_huaban_meinv:
                ShowToast.showTestShortToast(MainActivity.this,"花瓣美女");
                getSupportActionBar().setTitle(R.string.huaban_meinv_title);
                changeSearchMenuItemState(false);
                HuabanMeinvFragment huabanMeinvFragment = new HuabanMeinvFragment();
                replaceFragment(huabanMeinvFragment);
                isCloseDrawer = true;
                break;

            case R.id.nav_gank_meizi:
                ShowToast.showTestShortToast(MainActivity.this,"Gank妹子");
                getSupportActionBar().setTitle(R.string.gank_meizi_title);
                changeSearchMenuItemState(false);
                GankMeiziFragment gankMeiziFragment = new GankMeiziFragment();
                replaceFragment(gankMeiziFragment);
                isCloseDrawer = true;
                break;

            case R.id.nav_favorite:
                ShowToast.showTestShortToast(MainActivity.this,"收藏夹");
                gotoOtherActivity(ShowFavoritesActivity.class);
                break;

            case R.id.nav_download:
                ShowToast.showTestShortToast(MainActivity.this,"下载");
                gotoOtherActivity(ShowDownloadActivity.class);
                break;

            case R.id.nav_about:
                ShowToast.showTestShortToast(MainActivity.this,"关于");
                gotoOtherActivity(AboutActivity.class);
                break;

            default:
                ShowToast.showTestShortToast(MainActivity.this,"妹子图");
                break;
        }

        if(isCloseDrawer == true){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    /**
     * 替换MainActivity中的Fragment
     * */
    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContentInMainActivity,fragment);
        fragmentTransaction.commit();
    }

    /**
     * 跳转到其它的Activity
     * */
    public void gotoOtherActivity(Class clazz){
        Intent intent = new Intent(MainActivity.this,clazz);
        startActivity(intent);
    }

    public void exit(){
        if(System.currentTimeMillis() - exitTime > 2000){
             ShowToast.showShortToast(MainActivity.this,"再按一次退出妹图");
             exitTime = System.currentTimeMillis();
        }else{
            MainActivity.this.finish();
        }
    }

    /**
     * 控制搜索菜单条目的可见性
     * */
    public void changeSearchMenuItemState(boolean isVisible){
        mSearchMenuItem.setVisible(isVisible);
    }
}
