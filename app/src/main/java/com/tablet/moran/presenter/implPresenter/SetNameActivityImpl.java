package com.tablet.moran.presenter.implPresenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.tablet.moran.activity.BandPhoneActivity;
import com.tablet.moran.activity.ConnectWifiActivity;
import com.tablet.moran.activity.SetPadNameActivity;
import com.tablet.moran.activity.SplashActivity;
import com.tablet.moran.config.Constant;
import com.tablet.moran.model.Back;
import com.tablet.moran.model.Device;
import com.tablet.moran.model.Paint;
import com.tablet.moran.model.net.NewsNetBack;
import com.tablet.moran.presenter.ISetNameActivityPresenter;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.net.RetrofitUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Stone on 2017/12/14.
 */

public class SetNameActivityImpl extends BasePresenterImpl implements ISetNameActivityPresenter {

    private SetPadNameActivity setPadNameActivity;

    private BandPhoneActivity bandPhoneActivity;

    private SplashActivity splashActivity;

    private ConnectWifiActivity connectWifiActivity;
    public SetNameActivityImpl(Context context, String token, SetPadNameActivity setPadNameActivity) {
        super(context);

        this.setPadNameActivity = setPadNameActivity;


    }

    public SetNameActivityImpl(Context context, String token, ConnectWifiActivity connectWifiActivity) {
        super(context);

        this.connectWifiActivity = connectWifiActivity;


    }

    public SetNameActivityImpl(Context context, String token, SplashActivity splashActivity) {
        super(context);

        this.splashActivity= splashActivity;


    }

    public SetNameActivityImpl(Context context, String token, BandPhoneActivity bandPhoneActivity) {
        super(context);

        this.bandPhoneActivity = bandPhoneActivity;


    }


    @Override
    public void setPadNameBand(Device device) {

//        bandPhoneActivity.showProgressDialog();
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", device.getDevice_id());
        map.put("device_name", device.getDevice_name());
        final Subscription subscription = RetrofitUtils.api()
                .postDeviceInfo(getBody(map))
                .map(new Func1<Back, Back>() {
                    @Override
                    public Back call(Back RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Back>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        bandPhoneActivity.hidProgressDialog();
//                        bandPhoneActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Back allJourneysBack) {

//                        getNews();

                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
//                                splashActivity.postSuccess();
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
//                            bandPhoneActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }



    @Override
    public void setPadName(Device device) {

        Map<String, Object> map = new HashMap<>();
        map.put("device_id", device.getDevice_id());
        map.put("device_name", device.getDevice_name());
        final Subscription subscription = RetrofitUtils.api()
                .postDeviceInfo(getBody(map))
                .map(new Func1<Back, Back>() {
                    @Override
                    public Back call(Back RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Back>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        setPadNameActivity.hidProgressDialog();
                        setPadNameActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Back allJourneysBack) {

                        setPadNameActivity.hidProgressDialog();
                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
                                setPadNameActivity.postSuccess(new Paint());
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            setPadNameActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }



    @Override
    public void setPadNameSplash(Device device) {

        Map<String, Object> map = new HashMap<>();
        map.put("device_id", device.getDevice_id());
        map.put("device_name", device.getDevice_name());
        final Subscription subscription = RetrofitUtils.api()
                .postDeviceInfo(getBody(map))
                .map(new Func1<Back, Back>() {
                    @Override
                    public Back call(Back RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Back>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        splashActivity.hidProgressDialog();
                        splashActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Back allJourneysBack) {

                        getNews();

                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
//                                splashActivity.postSuccess();
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            splashActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }

    @Override
    public void getNews() {

        final Subscription subscription = RetrofitUtils.api()
                .getNews()
                .map(new Func1<NewsNetBack, NewsNetBack>() {
                    @Override
                    public NewsNetBack call(NewsNetBack RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsNetBack>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        splashActivity.hidProgressDialog();
                        splashActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(NewsNetBack allJourneysBack) {

                        splashActivity.hidProgressDialog();
                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
                                splashActivity.postSuccess(allJourneysBack.getPaint_info());
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            splashActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }

    @Override
    public void getNewsWifi() {


        final Subscription subscription = RetrofitUtils.api()
                .getNews()
                .map(new Func1<NewsNetBack, NewsNetBack>() {
                    @Override
                    public NewsNetBack call(NewsNetBack RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsNetBack>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        connectWifiActivity.hidProgressDialog();
                        connectWifiActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(NewsNetBack allJourneysBack) {

                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
                                connectWifiActivity.postSuccess(allJourneysBack.getPaint_info());
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            connectWifiActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }

    @Override
    public void setPadNameWifi(Device device) {

        SLogger.d("<<", "--->>>set device iddddddddd>");
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", device.getDevice_id());
        map.put("device_name", device.getDevice_name());
        final Subscription subscription = RetrofitUtils.api()
                .postDeviceInfo(getBody(map))
                .map(new Func1<Back, Back>() {
                    @Override
                    public Back call(Back RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Back>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        connectWifiActivity.hidProgressDialog();
                        connectWifiActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Back allJourneysBack) {

                        getNewsWifi();
                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
//                                connectWifiActivity.postSuccess();

                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            connectWifiActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }
}
