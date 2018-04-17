package com.tablet.moran.model.net;

import com.tablet.moran.model.BaseModel;
import com.tablet.moran.model.Paint;

/**
 * Created by ASUS on 2017/11/9.
 * <p>
 * 画作详情back
 */
public class NewsNetBack extends BaseModel {


    private Paint paint_info;

    private int ret;
    private String err;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public Paint getPaint_info() {
        return paint_info;
    }

    public void setPaint_info(Paint paint_info) {
        this.paint_info = paint_info;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
