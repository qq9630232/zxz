package com.tablet.moran.tools;

import android.content.Context;
import android.os.Environment;

import com.tablet.moran.tools.diskCache.DiskLruCacheHelper;
import com.tablet.moran.tools.log.KLog;

import java.io.File;
import java.io.IOException;


/**
 *
 * Logger日志类 打包时debug = false
 * Created by tristab on 15/10/23.
 */

public class SLogger {
    private final static boolean debug = false;

    private final static String LOG_FILE = "relationship_log";
    public static DiskLruCacheHelper diskLruCacheHelper;
    private SLogger() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    public static void init(Context context){
        try {
            diskLruCacheHelper = new DiskLruCacheHelper(context,LOG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void d(String tag, String msg) {
        if (debug) {
            KLog.d(tag, msg);
        }
        saveLog(tag,msg);
    }

    public static void e(String tag, String msg) {
        if (debug) {
            KLog.e(tag, msg);
        }
        saveLog(tag,msg);
    }

    public static void i(String tag, String msg) {
        if (debug) {
            KLog.i(tag, msg);
        }
        saveLog(tag,msg);
    }

    public static void v(String tag, String msg) {
        if (debug) {
            KLog.v(tag, msg);
        }
        saveLog(tag,msg);
    }

    public static void w(String tag, String msg) {
        if (debug) {
            KLog.w(tag, msg);
        }
        saveLog(tag,msg);
    }

    private static void saveLog(String tag,String msg){
//        KLog.file(tag,getCacheDir(),LOG_FILE,msg);
    }

    private static File getCacheDir() {
        File cachePath;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = Environment.getExternalStorageDirectory();
            } else {
                cachePath = Environment.getDataDirectory();
            }
        } catch (NullPointerException e) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = Environment.getExternalStorageDirectory();
            } else {
                cachePath = Environment.getDataDirectory();
            }
        }
        return cachePath;
    }
}
