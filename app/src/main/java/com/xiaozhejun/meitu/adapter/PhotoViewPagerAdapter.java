package com.xiaozhejun.meitu.adapter;

import android.content.Context;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yangzhe on 16-8-6.
 */
public class PhotoViewPagerAdapter extends PagerAdapter {

    private ArrayList<MeituPicture> meituPictureArrayList;

    public PhotoViewPagerAdapter(ArrayList<MeituPicture> meituPictureArrayList){
        this.meituPictureArrayList = meituPictureArrayList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        //从photo_view.xml加载PhotoView
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.photo_view,container,false);
        PhotoView photoView = (PhotoView)view.findViewById(R.id.photoViewInXml);
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBarInPhotoView);
        final TextView textView = (TextView)view.findViewById(R.id.textViewInPhotoView);
        final PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoView);
        container.addView(view, ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);
        Picasso.with(context)
                .load(meituPictureArrayList.get(position).getPictureUrl())
                .into(photoView,new Callback(){

                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        photoViewAttacher.update();
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                });
        return view; // 这里返回ViewPager中一个item所对应的View
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
    @Override
    public int getCount() {
        return meituPictureArrayList == null ? 0 : meituPictureArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}