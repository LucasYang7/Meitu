package com.xiaozhejun.meitu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhe on 16-8-5.
 */
public class MeituPictureListRecyclerViewAdapter extends RecyclerView.Adapter<MeituRecyclerView.PictureViewHolder> {

    private ArrayList<MeituPicture> mMeituPictureList;
    private MeituRecyclerView meituRecyclerView;   //与该MeituPictureListRecyclerViewAdapter绑定的MeituRecyclerView
    private boolean mShowIndex;         // 是否在图片名字中显示下标信息

    public MeituPictureListRecyclerViewAdapter(MeituRecyclerView recyclerView,boolean showIndex){
        meituRecyclerView = recyclerView;
        mShowIndex = showIndex;
    }

    public void initMeituPictureList(ArrayList<MeituPicture> meituPictureList){
        mMeituPictureList = meituPictureList;
    }

    public void updateMeituPictureList(ArrayList<MeituPicture> meituPictureList,int page){
        if(page == 1){
            initMeituPictureList(meituPictureList);
        }else{
            mMeituPictureList.addAll(meituPictureList);
        }
        notifyDataSetChanged();          // 通知注册了该Adapter的RecyclerView更新视图
    }

    public void updateMeituPictureList(ArrayList<MeituPicture> meituPictureList,boolean isResetData){
        if(isResetData == true){
            initMeituPictureList(meituPictureList);
        }else{
            mMeituPictureList.addAll(meituPictureList);
        }
        notifyDataSetChanged();          // 通知注册了该Adapter的RecyclerView更新视图
    }

    public ArrayList<MeituPicture> getmMeituPictureList(){
        return mMeituPictureList;
    }

    @Override
    public MeituRecyclerView.PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_viewholder,
                parent,false);
        return new MeituRecyclerView.PictureViewHolder(itemView,meituRecyclerView);
    }

    @Override
    public void onBindViewHolder(MeituRecyclerView.PictureViewHolder holder, int position) {
        MeituPicture meituPicture = mMeituPictureList.get(position);
        String title = meituPicture.getTitle();
        String pictureUrl = meituPicture.getPictureUrl();
        String pictureDescription = title;
        if(mShowIndex == true){
            pictureDescription = title+ " (" + (position+1) + ")";
        }
        holder.textViewInViewholder.setText(pictureDescription);
        Picasso.with(holder.imageViewInViewholder.getContext())
                .load(pictureUrl)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.meizitu)
                .into(holder.imageViewInViewholder);
    }

    @Override
    public int getItemCount() {
        return mMeituPictureList == null ? 0 : mMeituPictureList.size();
    }
}
