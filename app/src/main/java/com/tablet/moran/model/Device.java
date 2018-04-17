package com.tablet.moran.model;

/**
 * Created by Stone on 2017/12/14.
 */

public class Device extends BaseModel {

    private String device_id;

    private String device_name;


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}
