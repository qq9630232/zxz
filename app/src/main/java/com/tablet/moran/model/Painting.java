package com.tablet.moran.model;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ASUS on 2017/10/24.
 * 画作
 */

public class Painting {

    private String imageUrl;
    private String thumbPath;


    public String getthumbPath() {
        return thumbPath;
    }

    public void setthumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 保存图片的处理后的路径
     * @param thumbPath
     */
    public void saveThumbPath(Context context, File thumbPath) {
        if (!thumbPath.exists()) {
//            Bitmap b = PhotoUtils.createReflectedImage(PhotoUtils.getImageFromNet(imageUrl));
//            Bitmap b = PhotoUtils.createReflectedImage(BitmapFactory.decodeResource(context.getResources(), R.mipmap.recycler_temp_land));
//            this.thumbPath = saveBitmapFile(b, thumbPath).getAbsolutePath();
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

}
