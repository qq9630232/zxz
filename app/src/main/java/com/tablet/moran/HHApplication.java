package com.tablet.moran;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.igexin.sdk.PushManager;
import com.tablet.moran.activity.BaseActivity;
import com.tablet.moran.activity.PrintService;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.tools.AppTypeface;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Locale;

import android_serialport_api.PrinterReceipt;
import android_serialport_api.SerialPort;

/**
 * application设置全局
 * Created by stone on 2016/3/9.
 */
public class HHApplication extends Application {

    public static boolean loginFlag = false;
    public static boolean HxLoginFlag = false;
    private static Context mContext;

    private String token;
    private String username;
    private String userId;


    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private PrinterReceipt pr;

    public static final String LOCAL_LIST_VARIABLE = "{\n" +
            " \"opr_type\": 1,\n" +
            " \"pictures\": [17000011,17000006,17000003, 17000076,17000077, 17000009],\n" +
            "\"paint_id\": -1," +
            "\"title_info\":\n" +
            " {\n" +
            " \"detail_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%8D%97%E5%8D%8E%E7%A7%8B%E6%B0%B4.jpg\",\n" +
            " \"gauss_img_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%8D%97%E5%8D%8E%E7%A7%8B%E6%B0%B4.png\"\n" +
            " }\n" +
            " }";

    public static final String LOCAL_NEWS_VARIALBE = "{\"opr_type\": 1," +
            " \"pictures\": [17000021,17000054], " +
            "\"paint_id\":1," +
            "\"title_info\": {\"detail_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E6%8D%89%E6%9F%B3%E8%8A%B1%E5%9B%BE.jpg\", \"gauss_img_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E6%8D%89%E6%9F%B3%E8%8A%B1%E5%9B%BE.png\", \"picture_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E6%8D%89%E6%9F%B3%E8%8A%B1%E5%9B%BE.png\"}}";

    public static final String LOCAL_PAINT_TEST1 = "{\"opr_type\": 1, " +
            "\"pictures\": [17000342, 17000114], " +
            "\"paint_id\":2," +
            "\"title_info\": {\"detail_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E6%B5%94%E9%98%B3%E7%90%B5%E7%90%B6.jpg\", \"gauss_img_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E6%B5%94%E9%98%B3%E7%90%B5%E7%90%B6.png\", \"picture_url\": \"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E6%B5%94%E9%98%B3%E7%90%B5%E7%90%B6.png\"}}";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        if (PreferencesUtils.getString(this, Constant.LOCAL_LIST) == null) {
            PreferencesUtils.putString(this, Constant.LOCAL_LIST, LOCAL_LIST_VARIABLE);
            PreferencesUtils.putString(this, Constant.CURRENT_LIST, LOCAL_LIST_VARIABLE);
//            PreferencesUtils.putString(this, Constant.NEW_LIST, LOCAL_NEWS_VARIALBE);
//            PreferencesUtils.putString(this, Constant.PAINT_TEST, LOCAL_PAINT_TEST1);
            PreferencesUtils.putInt(this, MainActivity.PAINT_ID, 2);
        }


//        PreferencesUtils.putString(this, Constant.CLIENT_NAME, "测试1");
//        PreferencesUtils.putString(this, Constant.USER_ID, "4");

        //替换字体
        AppTypeface.init(this);

        AppUtils.initUtils(this);
        //环信
        //Easemob.init(this);
        //个推
        PushManager.getInstance().initialize(this);
        //校验登陆状态
        checkLogin();

        initPreference();

        String client_id = PushManager.getInstance().getClientid(this);

        if (!TextUtils.isEmpty(client_id))
            PreferencesUtils.putString(this, Constant.CLIENT_ID, client_id);

        PreferencesUtils.putInt(this, Constant.ORIENT, OrientEvent.POR);
        if (TextUtils.isEmpty(PreferencesUtils.getString(this, Constant.PLAY_TIME)))
            PreferencesUtils.putString(this, Constant.PLAY_TIME, Constant.DEFAUT_PLAY_TIME);


        SLogger.d("<<", "start application             client_id-->>>>" + client_id);

        //TODO 取消串口
        initSerialPort();

        if(PreferencesUtils.getInt(getApplicationContext(), BaseActivity.LANGUAGE) == BaseActivity.ENGLISH) {
            set(true);
//            PreferencesUtils.putInt(getApplicationContext(), BaseActivity.LANGUAGE, BaseActivity.ENGLISH);
//            PreferencesUtils.putInt(getApplicationContext(), BaseActivity.CUR_LANGUAGE, BaseActivity.ENGLISH);
        } else {
            set(false);
//            PreferencesUtils.putInt(getApplicationContext(), BaseActivity.LANGUAGE, BaseActivity.CHINESE);
//            PreferencesUtils.putInt(getApplicationContext(), BaseActivity.CUR_LANGUAGE, BaseActivity.CHINESE);
        }

    }

    public void set(boolean isEnglish) {

        Configuration configuration = getResources().getConfiguration();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (isEnglish) {
            //设置英文
            configuration.locale = Locale.ENGLISH;
        } else {
            //设置中文
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        }
        //更新配置
        getResources().updateConfiguration(configuration, displayMetrics);
    }

    /**
     * 设置屏幕方向
     */
    private void initOrient() {
        try {

            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void initSerialPort() {
        //---------------------------串口---------------------------
        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS3"), 19200, 0);


            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            pr = new PrinterReceipt(mOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //开启监听串口的service
        Intent intentService = new Intent(this, PrintService.class);
        startService(intentService);
    }

    public void initPreference() {


    }

    public void closeSerialPort() {
        mSerialPort.close();
    }


    public SerialPort getmSerialPort() {
        return mSerialPort;
    }

    public void setmSerialPort(SerialPort mSerialPort) {
        this.mSerialPort = mSerialPort;
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }


    /**
     * 设置用户登陆标志
     *
     * @return
     */
    public static void checkLogin() {
        String token = PreferencesUtils.getString(mContext, Constant.ACCESS_TOKEN);
        if (TextUtils.isEmpty(token)) {
            loginFlag = false;

        } else
            loginFlag = true;
    }

    /**
     * 检测环信登陆状态
     */
    public static void checkHxLogin() {
        String UserHxId = PreferencesUtils.getString(mContext, Constant.HX_ID);
        if (TextUtils.isEmpty(UserHxId)) {
            HxLoginFlag = false;
        } else {
            HxLoginFlag = true;
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public void getSerialData(String str) {
        if (serialDataInterface != null) {
            serialDataInterface.onReceive(str);
        }
    }

    public SerialDataInterface getSerialDataInterface() {
        return serialDataInterface;
    }

    public void setSerialDataInterface(SerialDataInterface serialDataInterface) {
        this.serialDataInterface = serialDataInterface;
    }

    SerialDataInterface serialDataInterface;


}
