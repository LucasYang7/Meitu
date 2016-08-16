package com.xiaozhejun.meitu.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.ui.widget.ShowToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 用于下载图片的AsyncTask
 * Created by yangzhe on 16-8-8.
 */
public class DownloadTask extends AsyncTask<String,Void,Uri> {
    private Context mContext;
    private String mTitle;
    private int mPosition;
    private String mExtensions = ".jpg";    // 图片的后缀名
    private String mAction;               // 用于标记是下载图片还是共享图片

    public DownloadTask(Context context,String action,String title,int position){
        mContext = context;
        mAction = action;
        mTitle = title;
        mPosition = position;
    }

    @Override
    protected Uri doInBackground(String... url) {
        Uri pictureUri = null;
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(mContext).load(url[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bitmap == null){
            //doInBackground在工作线程中执行，而显示Toast需要在UI线程中执行
            //ShowToast.showShortToast(mContext,"无法获取图片...");
        }else{
            File meituDir = new File(Environment.getExternalStorageDirectory(),"Meitu");
            if(meituDir.exists() == false){
                meituDir.mkdir();
            }
            //String pictureName = mTitle.replace('/','_') + "(" + mPosition + ")" +  mExtensions;  //保存到手机中的图片名字
            Resources resources = mContext.getResources();
            String pictureName = String.format(resources.getString(R.string.picture_name),mTitle.replace('/','_'),
                    mPosition+1,mExtensions);
            File picture = new File(meituDir,pictureName);

            try {
                FileOutputStream outputStream = new FileOutputStream(picture);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pictureUri = Uri.fromFile(picture);
            //通知图库更新
            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,pictureUri);
            mContext.sendBroadcast(scannerIntent);
        }
        return pictureUri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        if(uri == null){
            //onPostExecute在UI线程中执行
            //显示Toast需要在UI线程中执行
            ShowToast.showShortToast(mContext,"无法获取图片...");
        }else{
            if(mAction.equals("download")){
                String meituDir=uri.getPath();
                Resources resources = mContext.getResources();
                String downloadMsg = String.format(resources.getString(R.string.picture_has_save_to),meituDir);
                ShowToast.showShortToast(mContext,downloadMsg);
            }else {
                String shareTitle = "分享妹子图片到...";
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                shareIntent.setType("image/*");
                mContext.startActivity(Intent.createChooser(shareIntent,shareTitle));
            }
        }

    }
}
