package com.xiaozhejun.meitu.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeituPictureListRecyclerViewAdapter;
import com.xiaozhejun.meitu.ui.activity.PhotoViewActivity;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.util.ShowToast;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MeituPictureListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    protected SwipeRefreshLayout mMeituPictureListSwipeRefreshLayout;
    protected MeituRecyclerView mMeituPictureListMeituRecyclerView;
    protected MeituPictureListRecyclerViewAdapter meituPictureListRecyclerViewAdapter;
    protected boolean mIsLoadingData;    // 判断是否正在加载新的数据
    protected int mPage;      //表示妹子图片所在网页的分页
    protected String mType;   //表示妹子图片所属的类型
    protected Context mContext;  // 测试用

    public MeituPictureListFragment() {
        // Required empty public constructor
    }

    /**
     * 标记妹子图片所属的分类
     * */
    protected void setType(String type){
        mType = type;
    }

    /**
     * 执行下拉SwipeRefreshLayout进行刷新操作时，要清空原有的数据
     * */
    protected void resetMeiziData(){
        mPage = 1;                     // 重新加载首页的妹子图片信息
        mIsLoadingData = true;         // 在加载完首页的数据前，不能再加载新的妹子数据
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_meitu_picture_list, container, false);
        // 设置RecyclerView
        mMeituPictureListMeituRecyclerView = (MeituRecyclerView)view.findViewById(R.id.meituPictureListRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        meituPictureListRecyclerViewAdapter = new MeituPictureListRecyclerViewAdapter(mMeituPictureListMeituRecyclerView,false);
        meituPictureListRecyclerViewAdapter.initMeituPictureList(null);
        mMeituPictureListMeituRecyclerView.setHasFixedSize(true);
        mMeituPictureListMeituRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mMeituPictureListMeituRecyclerView.setAdapter(meituPictureListRecyclerViewAdapter);
        mMeituPictureListMeituRecyclerView.addOnScrollListener(new MeituRecyclerView.OnVerticalScrollListener() {
            @Override
            public void onBottom() {
                ShowToast.showTestShortToast(mContext,mType + " 已经滑到底部了" + "page " + mPage);
                if(mIsLoadingData == false){
                    ShowToast.showTestShortToast(mContext,mType + " 正在加载第" + mPage + "页的妹子数据");
                    loadMoreMeituPicture(mPage);
                }
            }
        }); // 为RecyclerView添加滑动事件监听
        mMeituPictureListMeituRecyclerView.setOnItemClickListener(new MeituRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                // 跳转到PhotoViewActivity
                Bundle bundle = new Bundle();
                bundle.putInt("position",postion);
                bundle.putBoolean("isDownload",false);
                bundle.putParcelableArrayList("meituPictureList",
                        meituPictureListRecyclerViewAdapter.getmMeituPictureList());
                Intent intent = new Intent(getActivity(),PhotoViewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // 设置SwipeRefresh
        mMeituPictureListSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.meituPictureListSwipeRefreshLayout);
        mMeituPictureListSwipeRefreshLayout.setOnRefreshListener(this);
        mMeituPictureListSwipeRefreshLayout.setColorSchemeResources(R.color.colorSwipeRefresh);
        mMeituPictureListSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshMeituPicture();
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        refreshMeituPicture();
    }

    /***
     * 加载更多的妹子图片
     */
    protected abstract void loadMoreMeituPicture(int page);

    /**
     * 刷新妹子图片
     * */
    protected abstract void refreshMeituPicture();

}
