package com.xiaozhejun.meitu.util.task;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.util.Logcat;
import com.xiaozhejun.meitu.util.ShowToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 用于下载图片的AsyncTask
 * Created by yangzhe on 16-8-8.
 */
public class DownloadTask extends AsyncTask<String, Void, Uri> {
    private Context mContext;
    private String mTitle;
    private int mPosition;
    private String mExtensions = ".jpg";    // 图片的后缀名
    private String mAction;                 // 用于标记是下载图片还是共享图片
    private String downloadPictureFolder;   // 用于保存下载图片的文件夹

    public DownloadTask(Context context, String action, String title, int position) {
        mContext = context;
        mAction = action;
        mTitle = title;
        mPosition = position;
        if (action.equalsIgnoreCase("download")) {
            downloadPictureFolder = "Meitu";
        } else {
            downloadPictureFolder = "TempMeitu";
        }
    }

    @Override
    protected Uri doInBackground(final String... url) {
        Uri pictureUri = null;
        Bitmap bitmap = null;
        try {
            if (url[1] == null || url[1].isEmpty()) { // url[1]是HTTP HEADER中的referer字段
                bitmap = Glide.with(mContext)
                        .load(url[0])
                        .asBitmap()
                        .into(-1, -1) //使用-1作为参数，这样可以保持图片资源的原始尺寸
                        .get();
            } else {
                Logcat.showLog("DownloadWithGlide", "Picture url = " + url[0] + ", referer = " + url[1]);
                Headers headers = new Headers() {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> header = new HashMap<>();
                        header.put("Referer", url[1]);
                        return header;
                    }
                };
                GlideUrl gliderUrl = new GlideUrl(url[0], headers);
                bitmap = Glide.with(mContext)
                        .load(gliderUrl)
                        .asBitmap()
                        .into(-1, -1) //使用-1作为参数，这样可以保持图片资源的原始尺寸
                        .get();
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            //doInBackground在工作线程中执行，而显示Toast需要在UI线程中执行
            //ShowToast.showShortToast(mContext, "图片太大，下载失败...");
            outOfMemoryError.printStackTrace();
            return pictureUri;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (bitmap == null) {
            //doInBackground在工作线程中执行，而显示Toast需要在UI线程中执行
            //ShowToast.showShortToast(mContext,"无法获取图片...");
        } else {
            File meituDir = new File(Environment.getExternalStorageDirectory(), downloadPictureFolder);
            if (meituDir.exists() == false) {
                meituDir.mkdir();
            }
            //String pictureName = mTitle.replace('/','_') + "(" + mPosition + ")" +  mExtensions;  //保存到手机中的图片名字
            Resources resources = mContext.getResources();
            mTitle = mTitle.replace('/', '_');       // 替换掉标题中的'/'
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateAndTime = simpleDateFormat.format(new Date());
            String pictureName = String.format(resources.getString(R.string.picture_name), mTitle,
                    currentDateAndTime, mExtensions);
            pictureName = pictureName.replaceAll("[~!@#$%^&]", "_");        // 替换图片名字中的特殊字符
            File picture = new File(meituDir, pictureName);

            try {
                FileOutputStream outputStream = new FileOutputStream(picture);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pictureUri = Uri.fromFile(picture);
            //通知图库更新
            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, pictureUri);
            mContext.sendBroadcast(scannerIntent);
        }
        return pictureUri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        if (uri == null) {
            //onPostExecute在UI线程中执行
            //显示Toast需要在UI线程中执行
            ShowToast.showShortToast(mContext, "无法获取图片.也有可能是图片太大了，无法保存到手机中...");
        } else {
            if (mAction.equals("download")) {
                String meituDir = uri.getPath();
                Resources resources = mContext.getResources();
                String downloadMsg = String.format(resources.getString(R.string.picture_has_save_to), meituDir);
                ShowToast.showShortToast(mContext, downloadMsg);
            } else {
                String shareTitle = "分享妹子图片到...";
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                mContext.startActivity(Intent.createChooser(shareIntent, shareTitle));
            }
        }

    }
}
