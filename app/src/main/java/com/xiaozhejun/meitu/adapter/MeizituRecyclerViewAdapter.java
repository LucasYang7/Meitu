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

    private List<MeizituGallery> meizituGalleryList;
    private Context context;

    public MeizituRecyclerViewAdapter(Context context){
        this.context = context;
    }

    public void setMeizituGalleryList(List<MeizituGallery> meizituGalleryList){
        this.meizituGalleryList = meizituGalleryList;
    }

    @Override
    public MeizituViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meizitu_viewholder,
                parent,false);
        return new MeizituViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MeizituViewHolder holder, int position) {
        MeizituGallery meizituGallery = meizituGalleryList.get(position);
        String title = meizituGallery.getTitle();
        String pictureUrl = meizituGallery.getPictureUrl();
        holder.textViewInViewholder.setText(title);
        Picasso.with(context)
                .load(pictureUrl)
                .placeholder(R.drawable.meizitu)
                .error(R.drawable.meizitu)
                .into(holder.imageViewInViewholder);
    }

    @Override
    public int getItemCount() {
        return meizituGalleryList.size();
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
