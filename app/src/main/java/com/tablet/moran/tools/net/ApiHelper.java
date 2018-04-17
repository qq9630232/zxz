package com.tablet.moran.tools.net;

import com.tablet.moran.model.Back;
import com.tablet.moran.model.net.NewsNetBack;
import com.tablet.moran.model.net.PictureNetBack;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 所有的接口类
 * Created by tristab on 15/10/23.
 */

public interface ApiHelper {

//
//
//    //获取城市列表
//    @FormUrlEncoded
//    @POST("journey/allBanner")
//    Observable<Back<List<Banner>>> getBanners(@Field("access_token") String access_token);

    //画作详情
    @GET("painting/picture/{picture_id}/info")
    Observable<PictureNetBack> getPictureDetail(@Path("picture_id") int picture_id);

    //画作详情
    @GET("painting/new_paint")
    Observable<NewsNetBack> getNews();


    //画单收藏
    @POST("device/post_device_info")
    Observable<Back> postDeviceInfo(@Body RequestBody body);

}
