package com.tablet.moran.model;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import com.tablet.moran.tools.photos.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Stone on 2017/11/26.
 */

public class Picture extends BaseModel {

    private String detail_url;
    private String gauss_img_url;
    private String title;
    private String thumbPath;
    private String picture_url;
    private int picture_id;
    private String detail;
    private String time;
    private String author;
    private String size;
    private String videoFile;

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(int picture_id) {
        this.picture_id = picture_id;
    }

    /**
     * 保存图片的处理后的路径
     *
     * @param thumbPath
     */
    public void saveThumbPath(Context context, File thumbPath) {

        int flag = 0;

        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            flag = 1;
        }

        if (!thumbPath.exists()) {
            Bitmap b = PhotoUtils.createReflectedImage(PhotoUtils.getImageFromNet(detail_url), flag);
            if(b == null) {
                return;
            }
//            Bitmap b = PhotoUtils.createReflectedImage(BitmapFactory.decodeResource(context.getResources(), R.mipmap.recycler_temp_land));
//            BitmapFactory.decodeResource(context.getResources(), R.mipmap.fore_blur_big);
            this.thumbPath = saveBitmapFile(b, thumbPath).getAbsolutePath();
        } else {
            this.thumbPath = thumbPath.getAbsolutePath();
        }

    }


    /**
     * bitmap----》file
     *
     * @param bitmap
     */
    private File saveBitmapFile(Bitmap bitmap, File file) {

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, bos);
            bos.flush();
            bos.close();
            bitmap.recycle();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getGauss_img_url() {
        return gauss_img_url;
    }

    public void setGauss_img_url(String gauss_img_url) {
        this.gauss_img_url = gauss_img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
