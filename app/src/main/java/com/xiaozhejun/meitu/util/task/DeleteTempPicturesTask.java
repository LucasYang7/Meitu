package com.xiaozhejun.meitu.util.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.xiaozhejun.meitu.util.ShowToast;

/**
 * Created by yangzhe on 16-8-18.
 */
public class DeleteTempPicturesTask extends AsyncTask<String,Void,Integer> {
    private Context mContext;

    public DeleteTempPicturesTask(Context context){
        this.mContext = context;
    }

    @Override
    protected Integer doInBackground(String... tempPicturePath) {
        int deleteTempPictureNum = 0;
        final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String tempPicturesFolder = "%" + tempPicturePath[0] + "%"; // 加上%是为了使用sql中的like语句
        final ContentResolver contentResolver = mContext.getContentResolver();
        deleteTempPictureNum = contentResolver.delete(uri,MediaStore.Images.Media.DATA + " like ? ",
                new String[]{tempPicturesFolder}); // ?表示where语句中的变量，而new String[]{tempPicturesFolder}则是该变量的参数值
        return deleteTempPictureNum;
    }

    @Override
    protected void onPostExecute(Integer deleteTempPictureNum) {
        super.onPostExecute(deleteTempPictureNum);
        ShowToast.showTestShortToast(mContext,"删除了" + deleteTempPictureNum + "张临时图片...");
    }
}
