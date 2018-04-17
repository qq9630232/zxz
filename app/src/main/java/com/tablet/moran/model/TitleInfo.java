package com.tablet.moran.model;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tablet.moran.R;
import com.tablet.moran.tools.photos.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Stone on 2017/12/17.
 */

public class TitleInfo extends BaseModel{

    private String detail_url;
    private String gauss_img_url;
    private String thumbPath;
    private int paint_id = 0;

    private String paint_title;
    private boolean news;

    public boolean isNews() {
        return news;
    }

    public void setNews(boolean news) {
        this.news = news;
    }

    public String getPaint_title() {
        return paint_title;
    }

    public void setPaint_title(String paint_title) {
        this.paint_title = paint_title;
    }

    public int getPaint_id() {
        return paint_id;
    }

    public void setPaint_id(int paint_id) {
        this.paint_id = paint_id;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    /**
     * 保存图片的处理后的路径
     *
     * @param thumbPath
     */
    public void saveThumbPath(Context context, File thumbPath) {

        //默认纵向
        int flag = 0;

        //横向
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            flag = 1;
        }

        if (!thumbPath.exists()) {
            Bitmap b = PhotoUtils.createReflectedImage(PhotoUtils.getImageFromNet(detail_url), flag);
            if(b == null) {
                b = PhotoUtils.createReflectedImage(BitmapFactory.decodeResource(context.getResources(), R.mipmap.recycler_temp_land), flag);
                this.thumbPath = saveBitmapFile(b, PhotoUtils.getDiskCacheDir(context, "thumb.png")).getAbsolutePath();
            } else {
                this.thumbPath = saveBitmapFile(b, thumbPath).getAbsolutePath();
            }

//            BitmapFactory.decodeResource(context.getResources(), R.mipmap.fore_blur_big);
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
}
