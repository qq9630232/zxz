package com.tablet.moran.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tablet.moran.activity.ShutdownTimeActivity;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.SLogger;

/**
 * Created by Stone on 2017/12/11.
 */

public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SLogger.i("<<", ": " + intent.getAction());
        if (intent.getAction().equals(Constant.PACKAGE_NAME)) {
            shutDown(context);
        }
    }

    private void shutDown(Context context) {

        Intent intentToTips = new Intent(context.getApplicationContext(), ShutdownTimeActivity.class);
        intentToTips.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentToTips);

    }
}
