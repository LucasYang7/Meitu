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

import java.util.List;

/**
 * Created by yangzhe on 16-7-29.
 */
public class MeizituRecyclerViewAdapter extends RecyclerView.Adapter<MeizituRecyclerViewAdapter.MeizituViewHolder> {

    private List<MeizituGallery> mMeizituGalleryList;
    private Context mContext;

    public MeizituRecyclerViewAdapter(Context context){
        mContext = context;
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

    @Override
    public MeizituViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meizitu_viewholder,
                parent,false);
        return new MeizituViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MeizituViewHolder holder, int position) {
        MeizituGallery meizituGallery = mMeizituGalleryList.get(position);
        String title = meizituGallery.getTitle();
        String pictureUrl = meizituGallery.getPictureUrl();
        holder.textViewInViewholder.setText(title);
        Picasso.with(mContext)
                .load(pictureUrl)
                .placeholder(R.drawable.meizitu)
                .error(R.drawable.meizitu)
                .into(holder.imageViewInViewholder);
    }

    @Override
    public int getItemCount() {
        return mMeizituGalleryList == null ? 0: mMeizituGalleryList.size();
    }

    /**
     * 妹子图RecyclerView里面的ViewHolder
     * */
    public static class MeizituViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewInViewholder;
        public ImageView imageViewInViewholder;
        public MeizituViewHolder(View itemView) {
            super(itemView);
            textViewInViewholder = (TextView) itemView.findViewById(R.id.textInMeizituViewHolder);
            imageViewInViewholder = (ImageView) itemView.findViewById(R.id.imageInMeizituViewHolder);
        }
    }

}
