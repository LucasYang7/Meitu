package com.xiaozhejun.meitu.util.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.xiaozhejun.meitu.adapter.MeituPictureListRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeituPicture;

import java.util.ArrayList;

/**
 * Created by yangzhe on 16-8-16.
 */
public class GetDownloadPicturesTask extends AsyncTask<String,Void,ArrayList<MeituPicture>> {

    private Context mContext;
    private ArrayList<MeituPicture> mDownloadPictureList;
    private MeituPictureListRecyclerViewAdapter mDownloadRecyclerViewAdapter;

    public GetDownloadPicturesTask(Context context,ArrayList<MeituPicture> downloadPictureList,
                                   MeituPictureListRecyclerViewAdapter downloadRecyclerViewAdapter){
        this.mContext = context;
        this.mDownloadPictureList = downloadPictureList;
        this.mDownloadRecyclerViewAdapter = downloadRecyclerViewAdapter;
    }

    @Override
    protected ArrayList<MeituPicture> doInBackground(String... downloadPicturePath) {
        ArrayList<MeituPicture> downloadPictureList = new ArrayList<MeituPicture>();
        final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final ContentResolver contentResolver = mContext.getContentResolver();
        final String meituFolderPath = "%" + downloadPicturePath[0] + "%"; // 加上%是为了使用sql中的like语句
        Cursor cursor = contentResolver.query(uri,null,MediaStore.Images.Media.DATA + " like ? ",
                new String[]{meituFolderPath},MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(cursor != null){
            while(cursor.moveToNext()){
                String picturePath = "file://" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                MeituPicture meituPicture = new MeituPicture();
                meituPicture.setPictureUrl(picturePath);
                meituPicture.setTitle(title);
                downloadPictureList.add(meituPicture);
            }
        }
        cursor.close();
        return downloadPictureList;
    }

    @Override
    protected void onPostExecute(ArrayList<MeituPicture> meituPictures) {
        super.onPostExecute(meituPictures);
        mDownloadPictureList.addAll(meituPictures);
        mDownloadRecyclerViewAdapter.updateMeituPictureList(meituPictures,true);
    }
}
