package com.tablet.moran.presenter;

import com.tablet.moran.model.Device;

/**
 * Created by ASUS on 2017/11/8.
 *
 * Login
 */
public interface ISetNameActivityPresenter {

    void setPadName(Device device);


    void setPadNameSplash(Device device);
    void setPadNameBand(Device device);

    void setPadNameWifi(Device device);


    void getNews();


    void getNewsWifi();

}
