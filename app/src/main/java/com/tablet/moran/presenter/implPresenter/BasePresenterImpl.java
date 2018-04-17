package com.tablet.moran.presenter.implPresenter;


import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tablet.moran.presenter.BasePresenter;
import com.tablet.moran.tools.diskCache.DiskLruCacheHelper;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by stone on 2016/4/29 0029.
 * 解决内存泄漏，及时取消订阅
 */
public class BasePresenterImpl implements BasePresenter {

    private CompositeSubscription mCompositeSubscription;
    protected Gson gson = new Gson();
    protected DiskLruCacheHelper diskLruCacheHelper;

    protected BasePresenterImpl(Context context) {

        try {
            diskLruCacheHelper = new DiskLruCacheHelper(context);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    protected void addSubscription(Subscription s) {

        if (this.mCompositeSubscription == null) {

            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    public void unsubcrible() {

        // TODO: 16/8/17 find when unsubcrible
        if (this.mCompositeSubscription != null) {

            this.mCompositeSubscription.unsubscribe();
        }
    }

    protected RequestBody getBody(Map map) {
        return RequestBody.create(MediaType.parse("Content-Type, application/json"),
                new JSONObject(map).toString());

    }
}
