package com.xiaozhejun.meitu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by yangzhe on 16-7-29.
 */
public class MeizituGalleryListRecyclerViewAdapter extends RecyclerView.Adapter<MeituRecyclerView.MeizituViewHolder> {

    private List<MeizituGallery> mMeizituGalleryList;
    private MeituRecyclerView meituRecyclerView;   //与该MeizituGalleryListRecyclerViewAdapter绑定的MeituRecyclerView

    public MeizituGalleryListRecyclerViewAdapter(MeituRecyclerView recyclerView) {
        meituRecyclerView = recyclerView;
    }

    /**
     * 初始化妹子图相册的数据
     */
    public void initMeizituGalleryList(List<MeizituGallery> meizituGalleryList) {
        mMeizituGalleryList = meizituGalleryList;
    }

    /**
     * 更新妹子图相册的数据
     *
     * @param meizituGalleryList 从妹子图网站上下载的数据
     * @param page               表示请求是妹子图网站的第page页数据
     */
    public void updateMeizituGalleryList(List<MeizituGallery> meizituGalleryList, int page) {
        if (page == 1) {
            initMeizituGalleryList(meizituGalleryList);
        } else {
            mMeizituGalleryList.addAll(meizituGalleryList);
        }
        notifyDataSetChanged();          // 通知注册了该Adapter的RecyclerView更新视图
    }

    /**
     * 获取相应位置的妹子图的相册数据
     *
     * @param position 某个ViewHolder在Adapter中的位置
     */
    public MeizituGallery getMeizituGallery(int position) {
        return mMeizituGalleryList.get(position);
    }

    @Override
    public MeituRecyclerView.MeizituViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meizitu_viewholder,
                parent, false);
        return new MeituRecyclerView.MeizituViewHolder(itemView, meituRecyclerView);
    }

    @Override
    public void onBindViewHolder(MeituRecyclerView.MeizituViewHolder holder, int position) {
        final MeizituGallery meizituGallery = mMeizituGalleryList.get(position);
        String title = meizituGallery.getTitle();
        String pictureUrl = meizituGallery.getPictureUrl();
        holder.textViewInViewholder.setText(title);
        Headers headers = new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Referer", meizituGallery.getReferer());
                return header;
            }
        };
        GlideUrl gliderUrl = new GlideUrl(pictureUrl, headers);
        Glide.with(holder.imageViewInViewholder.getContext())
                .load(gliderUrl)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.meizitu)
                .into(holder.imageViewInViewholder);
    }

    @Override
    public int getItemCount() {
        return mMeizituGalleryList == null ? 0 : mMeizituGalleryList.size();
    }

}
