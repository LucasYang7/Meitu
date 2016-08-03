package com.xiaozhejun.meitu.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;

/**
 * 自定义RecyclerView
 * (1)为该RecyclerView添加滑动到底部的事件监听OnVerticalScrollListener
 *（2)为该RecyclerView添加点击item的事件监听OnItemClickListener
 * Created by yangzhe on 16-8-3.
 */
public class MeituRecyclerView extends RecyclerView {

    private MeituRecyclerView.OnItemClickListener mOnItemClickListener;

    // 注意必须使用带有AttributeSet的构造放，否则MeituRecyclerView无法被XML文件解析，并提示错误:Error inflating class
    public MeituRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 为RecyclerView设置OnItemClickListener
     * */
    public void setOnItemClickListener(MeituRecyclerView.OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 妹子图RecyclerView里面的ViewHolder
     * */
    public static class MeizituViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewInViewholder;
        public ImageView imageViewInViewholder;
        public MeituRecyclerView meituRecyclerView;   //MeizituViewHolder所附属的MeituRecyclerView

        public MeizituViewHolder(View itemView, MeituRecyclerView parentMeituRecyclerView) {
            super(itemView);
            meituRecyclerView = parentMeituRecyclerView;
            textViewInViewholder = (TextView) itemView.findViewById(R.id.textInMeizituViewHolder);
            imageViewInViewholder = (ImageView) itemView.findViewById(R.id.imageInMeizituViewHolder);
            itemView.setOnClickListener(this);      // 别忘了设置OnClickListener!!!
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();    // 获取当前ViewHolder在Adapater中的位置
            meituRecyclerView.mOnItemClickListener.onItemClick(v,position);
        }
    }

    /**
     * 为RecyclerView设置是否到底部的事件监听
     * 通过staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions
     * 来找出最后一个完成出现的item
     * */
    public abstract static class OnVerticalScrollListener extends RecyclerView.OnScrollListener{

        /**
         * 判断当前显示的item是否为RecyclerView中的最后一个item
         * 目前只支持StaggeredGridLayoutManager布局
         * */
        private boolean isLastItemDisplaying(RecyclerView recyclerView){
            if(recyclerView.getAdapter().getItemCount() != 0){
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)
                        recyclerView.getLayoutManager();
                int [] lastCompletelyVisiblePostions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                int lastCompletelyVisibleItemPosition = 0;   //找到当前页面最后完整显示的item的位置 　
                for(int i = 0;i < lastCompletelyVisiblePostions.length;i++){
                    if(lastCompletelyVisiblePostions[i] > lastCompletelyVisibleItemPosition){
                        lastCompletelyVisibleItemPosition = lastCompletelyVisiblePostions[i];
                    }
                }
                if (lastCompletelyVisibleItemPosition != RecyclerView.NO_POSITION &&
                        lastCompletelyVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                    return true;
            }
            return false;
        }

        /**
         * 当前滑到RecyclerView的底部时调用该方法，但是方法的具体内容交给调用OnVerticalScrollListener的程序定义
         * */
        public abstract void onBottom();

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // RecyclerView已经滑到底部且RecyclerView处于静止状态
            if(isLastItemDisplaying(recyclerView) == true && newState == RecyclerView.SCROLL_STATE_IDLE){
                onBottom();
            }
        }

    }

    /**
     * 为RecyclerView添加OnItemClickListener
     * 当用户点击了某个ViewHolder时，触发相应的onItemClick的方法
     * */
    public interface OnItemClickListener{
        void onItemClick(View view,int postion);
    }

}
