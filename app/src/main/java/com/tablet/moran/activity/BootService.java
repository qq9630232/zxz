package com.tablet.moran.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tablet.moran.tools.SLogger;

public class BootService extends Service {
    private String apkname;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        SLogger.d("<<", "oncreate()");
        super.onCreate();
        mythread.start();

    }

    Thread mythread = new Thread(new Runnable() {

        @Override
        public void run() {
//			apkname = AppUtil.getString(BootService.this, "apk");
//			Log.d("TAG", "BootService" + apkname);
//			try{
//			if (apkname == null || apkname.equals("")) {

            SLogger.d("<<", "进入apk");
            Intent i = new Intent(BootService.this, StartAnimActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            onDestroy();

//			} else {
//				Intent intent = getPackageManager().getLaunchIntentForPackage(
//						apkname);
//				BootService.this.startActivity(intent);
//				Log.d("SHHH+++++bootService", "服务启动apk");
//
//			}
//			}catch(Exception e){
//				Log.d("SHHH","发生未知的错误，默认进入xieli.apk");
//				Intent i = new Intent(BootService.this, MainActivity.class);
//				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(i);
//				onDestroy();
//				e.printStackTrace();
//			}
        }

    });

}
