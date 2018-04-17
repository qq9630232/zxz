package com.tablet.moran.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tablet.moran.R;
import com.tablet.moran.tools.diskCache.DiskLruCacheHelper;
import com.tablet.moran.tools.net.RetrofitUtils;
import com.tablet.moran.view.CustomToast;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 统一管理需要初始化的工具类
 * Created by tristan on 15/10/23.
 */
public class AppUtils {

    private static Context mContext;
    private static DiskLruCacheHelper diskHelper;
    private final static String DIR_NAME = "BLUR_IMAGE";

    private AppUtils() {
        throw new UnsupportedOperationException("can not instantiated");
    }
    /**
     * 功能：
     * 初始化所有需要初始化的工具类
     * 获取应用程序的名称
     * 获取应用程序的版本号
     * @param context
     */
    /**
     * 统一初始化工具类，application onCreate中调用
     *
     * @param context
     */
    public static void initUtils(Context context) {
        RetrofitUtils.init(context);//初始化网络请求
        ImageLoader.init(context);//初始化图片加载
        SLogger.init(context);//初始化 logger
        DensityUtils.initDesityUtils(context);
        //CrashHandler.getInstance().init(context);

        mContext = context;
        try {
            if (diskHelper == null)
                diskHelper = new DiskLruCacheHelper(context, DIR_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskLruCacheHelper getDiskLruHelper() {
        if (diskHelper != null)
            return diskHelper;
        else
            throw new NullPointerException("the point is null");
    }

    /**
     * 验证手机格式 第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    public static boolean isMobile(String mobiles) {

       /* String telRegex = "^((13[0-9])|(14[5|7])|(17[0-9])|(15([0-3]|[5-9]))|(18[0-9]))\\d{8}$";//"[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return mobiles.matches(telRegex);*/

        //ios 的正则
        String telRegex = "1(?:[38]\\d|4[57]|5[0-35-9]|7[06-8])\\d{8}";//"[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return mobiles.matches(telRegex);

    }

    public static boolean isCode(String code) {
        String patternCoder = "(?<!\\d)\\d{4}(?!\\d)";
        return code.matches(patternCoder);
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取应用程序的版本名称
     *
     * @param con
     * @return
     */
    public static String getAppVersionName(Context con) {
        PackageManager manager = con.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取应用程序的版本号
     *
     * @param con
     * @return
     */
    public static int getAppVersion(Context con) {
        PackageManager manager = con.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void removeFromParent(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeView(view);
            }
        }
    }

    /**
     * 隐藏键盘覆写在activity中
     *
     * @param ev
     * @param context
     * @return 0：return dispatchTouchEvent(); 1：return true; 2：return onTouchEvent();3:需要隐藏键盘的同时做其他事情，根据3这个返回值判断
     */
    public static int hidenSoftInput(MotionEvent ev, Activity context) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = context.getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return 3;
            }
            return 0;
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (context.getWindow().superDispatchTouchEvent(ev)) {
            return 1;
        }
        return 2;
    }

    /**
     * 隐藏键盘覆写在activity中
     *
     * @param ev
     * @param context
     * @return 0：return dispatchTouchEvent(); 1：return true; 2：return onTouchEvent();
     */
    public static int hidenSoftInputASimple(MotionEvent ev, Activity context) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = context.getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return 0;
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (context.getWindow().superDispatchTouchEvent(ev)) {
            return 1;
        }
        return 2;
    }

    /**
     * 判断是不是在Edittext范围内
     *
     * @param v
     * @param event
     * @return
     */
    private static boolean isShouldHideInput(View v, MotionEvent event) {
        if (event != null && v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth() + (int) mContext.getResources().getDimension(R.dimen.send_width);
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前app是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    SLogger.d("后台", appProcess.processName);
                    return true;
                } else {
                    SLogger.d("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * show toast
     *
     * @param mContext
     * @param msg
     */
    public static void showToast(Context mContext, String msg) {

        CustomToast.showToast(mContext, msg);
    }

    /**
     * 隐藏footer view
     */
    public static void hideFooter(final View doingfooterView) {
        if (doingfooterView != null && doingfooterView.getVisibility() == View.VISIBLE) {
            doingfooterView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示footer view
     */
    public static void showFooter(final View doingfooterView) {
        if (doingfooterView != null && doingfooterView.getVisibility() == View.GONE) {
            doingfooterView.setPadding(0, 0, 0, 0);
            doingfooterView.setVisibility(View.VISIBLE);
        }
    }


    static String regddx = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";//科学计数法正则表达式
    static Pattern pattern = Pattern.compile(regddx);

    public static boolean isENum(String input) {//判断输入字符串是否为科学计数法
        return pattern.matcher(input).matches();
    }

    public static String getReturnHexString(byte[] paramArrayOfByte, int paramInt)
    {
        String str1 = "";
        for (int i = 0; i < paramInt; i++)
        {
            String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
            if (str2.length() == 1)
                str2 = '0' + str2;
            str1 = str1 + str2.toUpperCase();
        }
        return str1;
    }
}
