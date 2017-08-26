package com.xiaozhejun.meitu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.picasso.CustomPicasso;
import com.xiaozhejun.meitu.ui.activity.PhotoViewActivity;
import com.xiaozhejun.meitu.util.Logcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoViewInXml);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarInPhotoView);
        final TextView textView = (TextView) view.findViewById(R.id.textViewInPhotoView);
        container.addView(view, ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);
        // 如果妹子图片中的referer字段为空值，则直接使用默认的Picasso对象
        // 否则在Glide的HTTP请求头部中添加referer信息
        final MeituPicture meituPicture = meituPictureArrayList.get(picturePosition);
        if (meituPicture.getReferer() == null || meituPicture.getReferer().isEmpty()) {
            Picasso.with(context)
                    .load(meituPicture.getPictureUrl())
                    .into(photoView, new Callback() {

                        @Override
                        public void onSuccess() {
                            PhotoViewActivity.mIsFinishLoadingPicture[picturePosition] = true;//在图片加载成功后，才能执行下载图片和分享图片的操作
                            PhotoViewActivity.mCanDownloadPicture[picturePosition] = true;
                            progressBar.setVisibility(View.GONE);
                            PhotoViewAttacher picassoPhotoViewAttacher = new PhotoViewAttacher(photoView);
                            picassoPhotoViewAttacher.update();
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
        } else {
            Headers headers = new Headers() {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("Referer", meituPicture.getReferer());
                    return header;
                }
            };
            GlideUrl gliderUrl = new GlideUrl(meituPicture.getPictureUrl(), headers);
            Glide.with(context)
                    .load(gliderUrl)
                    .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                            PhotoViewActivity.mIsFinishLoadingPicture[picturePosition] = true;//在图片加载操作结束后，才能执行下载图片和分享图片的操作
                            PhotoViewActivity.mCanDownloadPicture[picturePosition] = false;
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                            Logcat.showLog("viewpagerPosition", "onError()" + picturePosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            PhotoViewActivity.mIsFinishLoadingPicture[picturePosition] = true;//在图片加载成功后，才能执行下载图片和分享图片的操作
                            PhotoViewActivity.mCanDownloadPicture[picturePosition] = true;
                            progressBar.setVisibility(View.GONE);
                            // 在网络图片下载完成之后，再初始化PhotoViewAttacher，
                            // 这样能解决缩放图片的时候，图片宽高突然变得很大的问题
                            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoView);
                            photoViewAttacher.update();
                            Logcat.showLog("viewpagerPosition", "onSuccess()" + picturePosition);
                            return false;
                        }
                    })
                    .into(photoView);
        }
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
