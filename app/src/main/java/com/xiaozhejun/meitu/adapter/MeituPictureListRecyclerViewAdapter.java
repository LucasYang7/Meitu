package com.xiaozhejun.meitu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.picasso.CustomPicasso;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.util.Logcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by yangzhe on 16-8-5.
 */
public class MeituPictureListRecyclerViewAdapter extends RecyclerView.Adapter<MeituRecyclerView.PictureViewHolder> {

    private ArrayList<MeituPicture> mMeituPictureList;
    private MeituRecyclerView meituRecyclerView;   //与该MeituPictureListRecyclerViewAdapter绑定的MeituRecyclerView
    private boolean mShowIndex;         // 是否在图片名字中显示下标信息

    public MeituPictureListRecyclerViewAdapter(MeituRecyclerView recyclerView, boolean showIndex) {
        meituRecyclerView = recyclerView;
        mShowIndex = showIndex;
    }

    public void initMeituPictureList(ArrayList<MeituPicture> meituPictureList) {
        mMeituPictureList = meituPictureList;
    }

    public void updateMeituPictureList(ArrayList<MeituPicture> meituPictureList, int page) {
        if (page == 1) {
            initMeituPictureList(meituPictureList);
        } else {
            mMeituPictureList.addAll(meituPictureList);
        }
        notifyDataSetChanged();          // 通知注册了该Adapter的RecyclerView更新视图
    }

    public void updateMeituPictureList(ArrayList<MeituPicture> meituPictureList, boolean isResetData) {
        if (isResetData == true) {
            initMeituPictureList(meituPictureList);
        } else {
            mMeituPictureList.addAll(meituPictureList);
        }
        notifyDataSetChanged();          // 通知注册了该Adapter的RecyclerView更新视图
    }

    public ArrayList<MeituPicture> getmMeituPictureList() {
        return mMeituPictureList;
    }

    @Override
    public MeituRecyclerView.PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_viewholder,
                parent, false);
        MeituRecyclerView.PictureViewHolder pictureViewHolder =
                new MeituRecyclerView.PictureViewHolder(itemView, meituRecyclerView);
        Logcat.showLog("onCreateViewHolder", "oldPosition = " + pictureViewHolder.getOldPosition()
                + " ,position = " + pictureViewHolder.getLayoutPosition());
        return pictureViewHolder;
    }

    @Override
    public void onBindViewHolder(MeituRecyclerView.PictureViewHolder holder, int position) {
        Logcat.showLog("onBindViewHolder", "itemId = " + holder.getItemId() + ",adapterPosition = " + holder.getAdapterPosition()
                + ",oldPosition = " + holder.getOldPosition() + " ,position = " + holder.getLayoutPosition());
        final MeituPicture meituPicture = mMeituPictureList.get(position);
        String title = meituPicture.getTitle();
        String pictureUrl = meituPicture.getPictureUrl();
        String pictureDescription = title;
        if (mShowIndex == true) {
            pictureDescription = title + " (" + (position + 1) + ")";
        }
        holder.textViewInViewholder.setText(pictureDescription);

        // 如果妹子图片中的referer字段为空值，则直接使用默认的Picasso对象
        // 否则在Glide的HTTP请求头部中添加referer信息
        if (meituPicture.getReferer() == null || meituPicture.getReferer().isEmpty()) {
            Picasso.with(holder.imageViewInViewholder.getContext())
                    .load(pictureUrl)
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.meizitu)
                    .into(holder.imageViewInViewholder);
        } else {
            Headers headers = new Headers() {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("Referer", meituPicture.getReferer());
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
    }

    @Override
    public int getItemCount() {
        return mMeituPictureList == null ? 0 : mMeituPictureList.size();
    }
}
