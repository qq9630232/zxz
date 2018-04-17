package com.tablet.moran.presenter.implPresenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.tablet.moran.activity.LiningActivity;
import com.tablet.moran.activity.PaintActivity;
import com.tablet.moran.config.Constant;
import com.tablet.moran.model.net.PictureNetBack;
import com.tablet.moran.presenter.ILiningActivityPresenter;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.net.RetrofitUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Stone on 2017/12/14.
 */

public class LiningActivityImpl extends BasePresenterImpl implements ILiningActivityPresenter {

    private LiningActivity liningActivity;
    private PaintActivity paintActivity;

    public LiningActivityImpl(Context context, String token, LiningActivity liningActivity) {
        super(context);

        this.liningActivity = liningActivity;


    }

    public LiningActivityImpl(Context context, String token, PaintActivity paintActivity) {
        super(context);

        this.paintActivity = paintActivity;


    }

    @Override
    public void getPicture(int pictureId) {

        liningActivity.showProgressDialog();

        final Subscription subscription = RetrofitUtils.api()
                .getPictureDetail(pictureId)
                .map(new Func1<PictureNetBack, PictureNetBack>() {
                    @Override
                    public PictureNetBack call(PictureNetBack RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PictureNetBack>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        liningActivity.hidProgressDialog();
                        liningActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(PictureNetBack allJourneysBack) {

                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
                                liningActivity.getSuccess(allJourneysBack.getPicture_detail());
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            liningActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }

    @Override
    public void getPicture1(int pictureId) {

        paintActivity.showProgressDialog();

        final Subscription subscription = RetrofitUtils.api()
                .getPictureDetail(pictureId)
                .map(new Func1<PictureNetBack, PictureNetBack>() {
                    @Override
                    public PictureNetBack call(PictureNetBack RegisterBack) {
                        return RegisterBack;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PictureNetBack>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        paintActivity.hidProgressDialog();
                        paintActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(PictureNetBack allJourneysBack) {

                        SLogger.d("<<", "-->" + JSON.toJSONString(allJourneysBack));

                        if (allJourneysBack.getRet() == Constant.SUCCESSRESPONSE) {

                            try {
                                paintActivity.getSuccess(allJourneysBack.getPicture_detail());
                            } catch (ClassCastException e) {
                                SLogger.d("<<", "异常");
                                e.printStackTrace();
                            }

                        } else {
                            paintActivity.showError(allJourneysBack.getErr());
                        }


                    }
                });

        addSubscription(subscription);
    }
}
