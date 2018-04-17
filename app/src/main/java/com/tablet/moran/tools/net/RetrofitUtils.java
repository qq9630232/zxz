
package com.tablet.moran.tools.net;

import android.content.Context;
import android.text.TextUtils;

import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit网络请求
 * Created by stone on 2016/3/18.
 */
public class RetrofitUtils {

    private static final String TAG = "net";

    //测试环境
//    public static final String BASE_URL = "http://dev.xiangshuispace.com:9988/api/";
    public static final String BASE_URL = "http://app.atmoran.com/api/";
//    public static final String BASE_URL = "http://dev.xiangshuispace.com:9988/api/";
//    public static final String BASE_URL = "http://10.0.2.2/runaway/api/";

    boolean isDebug = true;

    private static RetrofitUtils instance;
    private static Context mContext;
    private static ApiHelper api;
    private final static String CACHE_DIR = "HH_NET_cache";

    private String token;
    private String uin;

    public static void getInstance(Context context) {
        if (instance == null) {
            synchronized (RetrofitUtils.class) {
                if (instance == null) {
                    instance = new RetrofitUtils(context);
                }
            }
        }
    }

    /**
     * 在 application oncreate 里调用
     *
     * @param context
     */
    public static void init(Context context) {
        getInstance(context);
    }

    private RetrofitUtils(Context context) {
        mContext = context;
        File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 10); //10Mb
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(LoggingInterceptor)
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiHelper.class);
    }

    public static ApiHelper api() {
        return api;
    }

    /**
     * 服务器端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            //配置request
            Request request = chain.request();
//            if (!NetWorkUtil.isNetworkAvailable(mContext)) {
//                request = request.newBuilder()
//                        .cacheControl(okhttp3.CacheControl.FORCE_CACHE)
//                        .build();
//
//                SLogger.d(TAG, "no network");
//            }
            //设置统一的header
            token = PreferencesUtils.getString(mContext, Constant.ACCESS_TOKEN);
            uin = PreferencesUtils.getString(mContext, Constant.USER_ID);
            if (TextUtils.isEmpty(uin)) {
                uin = "100000";
            }
            Request request1;
            if (!TextUtils.isEmpty(token))
                request1 = request.newBuilder().header("User-Uin", uin).header("Client-Token", token).removeHeader("Content-Type").header("Content-Type", "application/json").build();
            else {
                request1 = request.newBuilder().header("User-Uin", uin).removeHeader("Content-Type").header("Content-Type", "application/json").build();
            }

            return chain.proceed(request1);
            //配置响应的responser
//            okhttp3.Response originalResponse = chain.proceed(request1);
//            if (NetWorkUtil.isNetworkConnected(mContext)) {
//                //有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
//                String cacheControl = request.cacheControl().toString();
//                int maxAge = 60; // read from cache for 1 minute
//                return originalResponse.newBuilder()
//                        .removeHeader("Progma")
//                        .removeHeader("Cache-Control")
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .build();
//            } else {
//                return originalResponse.newBuilder()
//                        .removeHeader("Pragma")
//                        .removeHeader("Cache-Control")
//                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200") //60 * 60 * 24 * 28 4周 单位s
//                        .build();
//            }
        }
    };


    /**
     * 日志拦截器
     */
    private final Interceptor LoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();

            SLogger.i(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();

            SLogger.i(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    };

}