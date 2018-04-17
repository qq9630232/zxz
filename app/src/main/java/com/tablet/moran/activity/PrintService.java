package com.tablet.moran.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.tablet.moran.HHApplication;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.SerialEvent;
import com.tablet.moran.tools.HexUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;

import android_serialport_api.PrinterReceipt;
import android_serialport_api.SerialPort;
import de.greenrobot.event.EventBus;

public class PrintService extends Service {
    private static final boolean DEBUG = true;
    private static final String TAG = "SP_HJ";
    public static String string = HexUtils.getHexResult("testtesttesttest");
    byte[] bytes = null;
    byte[] bytesdouble = null;
    SharedPreferences.Editor editor;
    /*    private Handler handler = new Handler() {
            public void handleMessage(Message paramAnonymousMessage) {
                super.handleMessage(paramAnonymousMessage);

                String str = (String) paramAnonymousMessage.obj;
                PrintService.this.LOGD("---------handleMessage-------------printerinfo==" + str);
                switch (paramAnonymousMessage.what) {
                    default:
                    case 1:
                    case 2:
                }
                do {
                    return;
    //                Application.count = 1L + Application.count;
    //                PrintService.this.LOGD("-------------count--------------" + Application.count);
    //                PrintService.this.editor.putLong("num", Application.count);
    //                PrintService.this.editor.commit();
                    PrintService.this.bytes = null;
    //                PrintService.this.LOGD("---------handleMessage-------------count==" + Application.count);
    //                HexUtils.getHexResult("打印测试次数：          " + Application.count);
                    PrintService.this.bytes = PrintService.this.pr.getByteInfo(PrintService.string, "AA 01 0E 00 00 28 FA 55");
                    PrintService.this.LOGD("---------handleMessage-------------mun==" + "AA 01 0E 00 00 28 FA 55");
                    return;

                }
                while (("0c".equals(str)) && (!"72".equals(str)));
                PrintService.access$002(PrintService.this, true);
            }
        };*/
    private boolean isInterrupt = true;
    private boolean isInterrupted = false;
    protected HHApplication mApplication;
    private InputStream mInputStream;
    protected OutputStream mOutputStream;
    private ReadThread mReadThread = null;
    protected SerialPort mSerialPort;
    private Thread pThread;
    private PrinterReceipt pr;
    SharedPreferences sp;

    //-------------定时关机-------------------
    Timer timer = new Timer();


    public void LOGD(String paramString) {
//        SLogger.d("<<", paramString);
    }

    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.isInterrupt = false;
        this.sp = getSharedPreferences("android_serialport_api.sample_preferences", 0);
        this.editor = this.sp.edit();
        this.mApplication = ((HHApplication) getApplication());

        try {
            this.mSerialPort = this.mApplication.getmSerialPort();
            this.mOutputStream = this.mSerialPort.getOutputStream();
            this.mInputStream = this.mSerialPort.getInputStream();
            if (this.mReadThread == null) {
                this.mReadThread = new ReadThread();
                this.mReadThread.start();
            }
            this.pr = new PrinterReceipt(this.mOutputStream);
        } catch (Exception localIOException) {
            localIOException.printStackTrace();
        }

        //------------------定时关机--------------
        timer = new Timer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.SHUTDOWN_ACTION);
        registerReceiver(screenOffReceiver, filter);
    }

    protected void onDataReceived(byte[] paramArrayOfByte, int paramInt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramInt; i++) {
            String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
            if (str2.length() == 1)
                str2 = '0' + str2;
            sb.append(str2.toUpperCase());
        }
        EventBus.getDefault().post(new SerialEvent(sb.toString()));
    }

    public void onDestroy() {
        super.onDestroy();
        LOGD("service onDestroy");
        this.isInterrupt = true;
        this.bytes = null;
        if (this.mReadThread != null)
            this.isInterrupted = true;
        this.mApplication.closeSerialPort();
        this.mSerialPort = null;
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Log.i("@qi", "service onStart");
//        this.pThread = new SendingThread();
//        this.pThread.start();
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }

    public void printcommand(final byte[] paramArrayOfByte) {
        new Thread(new Runnable() {
            public void run() {
                pr.PrintCommand(new String(paramArrayOfByte));
            }
        }).start();
    }

    private class ReadThread extends Thread {
        private ReadThread() {
        }

        public void run() {
            super.run();
            while (!PrintService.this.isInterrupted)
                try {
                    byte[] arrayOfByte = new byte[64];
                    if (PrintService.this.mInputStream == null)
                        return;
                    PrintService.this.LOGD("***********************read  before");
                    int i = PrintService.this.mInputStream.read(arrayOfByte);
                    PrintService.this.LOGD("***********************read  later");
                    if (i > 0)
                        PrintService.this.onDataReceived(arrayOfByte, i);
                } catch (IOException localIOException) {
                    localIOException.printStackTrace();
                }
        }
    }


    //------------------------------接受关机广播---------------------
    AlarmManager am;
    PendingIntent pendingIntent;

    public void shutDownAlarm(int time) {
        am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Constant.PACKAGE_NAME);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);

    }

    public void cancel(PendingIntent pendingIntent) {
        if (am != null) {
            am.cancel(pendingIntent);
        }
    }

    BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        int what = 2;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.SHUTDOWN_ACTION)) {
                Log.i("<<", "Receiver screen off broadcast......");
                int minutes = intent.getIntExtra(Constant.SHUTDOWN_TIME, 30);
                shutDownAlarm(minutes * 60 * 1000);
            } else if (intent.getAction().equals(Constant.SHUTDOWN_CANCEL_ACTION)) {
                if (am != null && pendingIntent != null)
                    am.cancel(pendingIntent);
            }

        }
    };
}