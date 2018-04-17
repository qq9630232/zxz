package com.tablet.moran.model.net;

import com.tablet.moran.model.BaseModel;
import com.tablet.moran.model.Picture;

/**
 * Created by ASUS on 2017/11/9.
 *
 * 画作详情back
 */
public class PictureNetBack extends BaseModel {


    private Picture picture_detail;

    private int ret;
    private String err;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public Picture getPicture_detail() {
        return picture_detail;
    }

    public void setPicture_detail(Picture picture_detail) {
        this.picture_detail = picture_detail;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
