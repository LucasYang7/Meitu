package com.xiaozhejun.meitu.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 每张妹子图片所对应的信息
 * Created by yangzhe on 16-8-3.
 */
public class MeituPicture implements Parcelable {

    public String title;
    public String pictureUrl;

    public MeituPicture(){

    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setPictureUrl(String pictureUrl){
        this.pictureUrl = pictureUrl;
    }

    public String getTitle(){
        return title;
    }

    public String getPictureUrl(){
        return pictureUrl;
    }

    protected MeituPicture(Parcel in) {
        title = in.readString();
        pictureUrl = in.readString();
    }

    public static final Creator<MeituPicture> CREATOR = new Creator<MeituPicture>() {
        @Override
        public MeituPicture createFromParcel(Parcel in) {
            return new MeituPicture(in);
        }

        @Override
        public MeituPicture[] newArray(int size) {
            return new MeituPicture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(pictureUrl);
    }

    // 因为LinkedHashSet判断两个对象是否相等时用到了equals方法和hashCode方法
    // 所以这里要重写MeituPicture类中的equals方法和hashCode方法
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }else{
            if(obj instanceof MeituPicture){
                if(this.getPictureUrl().equals(((MeituPicture) obj).getPictureUrl())){
                    return true;
                }else{
                    return false;
                }
            }
            return false;
        }
    }

    @Override
    public int hashCode() {
        String pictureUrl = this.getPictureUrl();
        int hashCodeValue = pictureUrl.hashCode();
        return hashCodeValue;
    }
}
