package com.tablet.moran.event;

/**
 * Created by Stone on 2017/12/10.
 */

public class SerialEvent {

    public SerialEvent(String msg) {
        this.msg = msg;
    }

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
