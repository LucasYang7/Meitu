package com.xiaozhejun.meitu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.picasso.CustomPicasso;
import com.xiaozhejun.meitu.ui.activity.PhotoViewActivity;
import com.xiaozhejun.meitu.util.Logcat;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yangzhe on 16-8-6.
 */
public class PhotoViewPagerAdapter extends PagerAdapter {

    private ArrayList<MeituPicture> meituPictureArrayList;

    public PhotoViewPagerAdapter(ArrayList<MeituPicture> meituPictureArrayList) {
        this.meituPictureArrayList = meituPictureArrayList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int picturePosition = position;
        PhotoViewActivity.mIsFinishLoadingPicture[picturePosition] = false;//在图片加载操作结束前，不能执行下载图片和分享图片的操作
        Context context = container.getContext();
        //从photo_view.xml加载PhotoView
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.photo_view, container, false);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photoViewInXml);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarInPhotoView);
        final TextView textView = (TextView) view.findViewById(R.id.textViewInPhotoView);
        final PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoView);
        container.addView(view, ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);
        Picasso picasso;
        MeituPicture meituPicture = meituPictureArrayList.get(picturePosition);
        if (meituPicture.getReferer() == null || meituPicture.getReferer().isEmpty()) {
            picasso = Picasso.with(context);
        } else {
            picasso = CustomPicasso.getCustomePicasso(context, meituPicture.getReferer());
        }
        picasso.load(meituPicture.getPictureUrl())
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE) // 不缓存PhotoViewPager中的图片，以免出现OutOfMemoryError
                .into(photoView, new Callback() {

                    @Override
                    public void onSuccess() {
                        PhotoViewActivity.mIsFinishLoadingPicture[picturePosition] = true;//在图片加载成功后，才能执行下载图片和分享图片的操作
                        PhotoViewActivity.mCanDownloadPicture[picturePosition] = true;
                        progressBar.setVisibility(View.GONE);
                        photoViewAttacher.update();
                        Logcat.showLog("viewpagerPosition", "onSuccess()" + picturePosition);
                    }

                    @Override
                    public void onError() {
                        PhotoViewActivity.mIsFinishLoadingPicture[picturePosition] = true;//在图片加载操作结束后，才能执行下载图片和分享图片的操作
                        PhotoViewActivity.mCanDownloadPicture[picturePosition] = false;
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        Logcat.showLog("viewpagerPosition", "onError()" + picturePosition);
                    }
                });
        return view; // 这里返回ViewPager中一个item所对应的View
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
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
