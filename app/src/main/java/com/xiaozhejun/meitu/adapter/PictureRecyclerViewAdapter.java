package com.xiaozhejun.meitu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;

import java.util.List;

/**
 * Created by yangzhe on 16-8-5.
 */
public class PictureRecyclerViewAdapter extends RecyclerView.Adapter<MeituRecyclerView.PictureViewHolder> {

    private List<MeituPicture> mMeituPictureList;
    private MeituRecyclerView meituRecyclerView;   //与该PictureRecyclerViewAdapter绑定的MeituRecyclerView

    public PictureRecyclerViewAdapter(MeituRecyclerView recyclerView){
        meituRecyclerView = recyclerView;
    }

    public void initMeituPictureList(List<MeituPicture> meituPictureList){
        mMeituPictureList = meituPictureList;
    }

    public void updateMeituPictureList(List<MeituPicture> meituPictureList){
        mMeituPictureList = meituPictureList;
        notifyDataSetChanged();
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
        holder.textViewInViewholder.setText(title+ " (" + (position+1) + ")");
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
