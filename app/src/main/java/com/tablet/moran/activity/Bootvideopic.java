package com.tablet.moran.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tablet.moran.tools.SLogger;

/**
 * Created by Stone on 2017/12/10.
 */

public class Bootvideopic extends BroadcastReceiver {
    private final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        SLogger.d("<<", "==================开机广播");
        if (intent.getAction().equals(ACTION)) {
            SLogger.d("<<", "=================启动服务");


//            context.startService(new Intent(context, BootService.class));




            // LastOperateBean bean = null;
            // String lastStr = FileService.readFile();

            // Log.d("BootRestartReceiver", "开机启动获取的JSON=" + lastStr);
            // if (lastStr != null && !"".equals(lastStr.trim())) {
            // bean = JSONObject.parseObject(lastStr, LastOperateBean.class);
            // Intent intentstart = new Intent();
            // if (bean.getResbeans() != null
            // && bean.getResbeans().size() != 0) {
            //
            // intentstart.setClassName(context, bean.getIntentName());
            // intentstart.putExtra("mystr", bean.getResbeans());
            //
            // }
            // FileService.saveFile("");
            // intentstart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(intentstart);
            // } else {
            // Log.d("SHHH+++Bootvideopic", "没有音乐视频，判断apk");
            // Intent bootintent = new Intent(context, MainActivity.class);
            // bootintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(bootintent);
            // }

        }

    }

}
