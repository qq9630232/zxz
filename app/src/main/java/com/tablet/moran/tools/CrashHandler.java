package com.tablet.moran.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import com.tablet.moran.MainActivity;


/**
 * Created by tristan on 16/2/15.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final static String TAG = "CrashHandler";
    private Context mContext;
    private static CrashHandler mInstance = new CrashHandler();

    public void init(Context context) {
        mContext = context.getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {

        return mInstance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.i(TAG, "自己的app处理");
        SLogger.e(TAG, ex.getMessage());
        restartApp();
    }

    public void restartApp() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        Process.killProcess(Process.myPid());
         //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }
}