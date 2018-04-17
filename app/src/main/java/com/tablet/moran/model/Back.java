package com.tablet.moran.model;


/**
 * 服务器返回集合类
 * Created by stone on 2016/3/6.
 */
public class Back {


    private int ret = 0;
    private String err = "";


    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
