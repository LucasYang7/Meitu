package com.xiaozhejun.meitu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.ui.widget.ShowToast;
import com.xiaozhejun.meitu.util.Logcat;

import java.util.List;

/**
 * Created by yangzhe on 16-7-29.
 */
public class MeizituRecyclerViewAdapter extends RecyclerView.Adapter<MeituRecyclerView.MeizituViewHolder> {

    private List<MeizituGallery> mMeizituGalleryList;
    private MeituRecyclerView meituRecyclerView;   //与该MeizituRecyclerViewAdapter绑定的MeituRecyclerView

    public MeizituRecyclerViewAdapter(MeituRecyclerView recyclerView){
        meituRecyclerView = recyclerView;
    }

    /**
     * 初始化妹子图相册的数据
     * */
    public void initMeizituGalleryList(List<MeizituGallery> meizituGalleryList){
        mMeizituGalleryList = meizituGalleryList;
    }

    /**
     * 更新妹子图相册的数据
     * @param meizituGalleryList 从妹子图网站上下载的数据
     * @param page 表示请求是妹子图网站的第page页数据
     * */
    public void updateMeizituGalleryList(List<MeizituGallery> meizituGalleryList,int page){
        if(page == 1){
            initMeizituGalleryList(meizituGalleryList);
        }else{
            mMeizituGalleryList.addAll(meizituGalleryList);
        }
        notifyDataSetChanged();          // 通知注册了该Adapter的RecyclerView更新视图
    }

    /**
     * 获取相应位置的妹子图的相册数据
     * @param position 某个ViewHolder在Adapter中的位置
     * */
    public MeizituGallery getMeizituGallery(int position){
        return mMeizituGalleryList.get(position);
    }

    @Override
    public MeituRecyclerView.MeizituViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meizitu_viewholder,
                parent,false);
        return new MeituRecyclerView.MeizituViewHolder(itemView,meituRecyclerView);
    }

    @Override
    public void onBindViewHolder(MeituRecyclerView.MeizituViewHolder holder, int position) {
        MeizituGallery meizituGallery = mMeizituGalleryList.get(position);
        String title = meizituGallery.getTitle();
        String pictureUrl = meizituGallery.getPictureUrl();
        holder.textViewInViewholder.setText(title);
        Picasso.with(holder.imageViewInViewholder.getContext())
                .load(pictureUrl)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.meizitu)
                .into(holder.imageViewInViewholder);
    }

    @Override
    public int getItemCount() {
        return mMeizituGalleryList == null ? 0: mMeizituGalleryList.size();
    }

}
