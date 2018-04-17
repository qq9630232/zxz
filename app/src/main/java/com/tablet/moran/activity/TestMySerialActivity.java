package com.tablet.moran.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tablet.moran.R;
import com.tablet.moran.tools.SLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class TestMySerialActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_my_serial);

        try {
            this.mSerialPort = new SerialPort(new File("/dev/ttyS3"), 19200, 0);
            this.mOutputStream = this.mSerialPort.getOutputStream();
            this.mInputStream = this.mSerialPort.getInputStream();
            if (this.mReadThread == null) {
                this.mReadThread = new ReadThread();
                this.isStrart = true;
                this.mReadThread.start();
            }
            return;
        } catch (IOException localIOException) {
            while (true)
                localIOException.printStackTrace();
        }
    }


    private class ReadThread extends Thread {
        private ReadThread() {
        }

        public void run() {
            super.run();
            byte[] arrayOfByte = new byte[64];
            try {
                while (true) {
                    int i = TestMySerialActivity.this.mInputStream.read(arrayOfByte);
                    if ((i == -1) || (!TestMySerialActivity.this.isStrart))
                        break;
                    if (i > 0)
                        TestMySerialActivity.this.onDataReceived(arrayOfByte, i);
                }
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }


    long curTime = 0l;
    private ImageView bottomBtn = null;
    private Handler handler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            super.handleMessage(paramAnonymousMessage);

            String str = (String) paramAnonymousMessage.obj;
            SLogger.d("SHHH", "------------printerInfo::--------------" + str);




            if (str.indexOf("01") != -1) {
                if (System.currentTimeMillis() - curTime < 1000) {
                    return;
                }
                Toast.makeText(getApplicationContext(), "方向--》右", Toast.LENGTH_SHORT).show();
                curTime = System.currentTimeMillis();
                return;
            } else if (str.indexOf("02") != -1) {
                if (System.currentTimeMillis() - curTime < 1000)
                    return;
                curTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "方向--》左", Toast.LENGTH_SHORT).show();
                return;
            } else if (str.indexOf("04") != -1) {
                if (System.currentTimeMillis() - curTime < 1000)
                    return;
                curTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "方向--》上", Toast.LENGTH_SHORT).show();
                return;
            } else if (str.indexOf("08") != -1) {
                if (System.currentTimeMillis() - curTime < 1000)
                    return;
                curTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "方向--》下", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    };
    private boolean isHide;
    private boolean isStrart = false;
    private ImageView leftBtn = null;
    private int mAlpha = 0;

    private InputStream mInputStream;
    protected OutputStream mOutputStream;
    private ReadThread mReadThread = null;
    protected SerialPort mSerialPort;
    private ImageView rightBtn = null;
    private ImageView topBtn = null;
    private ViewFlipper viewFlipper = null;
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;


    public String getReturnHexString(byte[] data, int length) {

        SLogger.d("<<", "长度----》" + length);
        for(int j = 0; j < length; j++) {
            SLogger.d("<<", ">>>>>>>>>>" + data[j]);
        }

        String str1 = "";
        for (int i = 0; i < length; i++) {

            String str2 = Integer.toHexString(0xFF & data[i]);
            if (str2.length() == 1)
                str2 = '0' + str2;
            str1 = str1 + str2.toUpperCase();
        }

        SLogger.d("<<", "转换后---》" + str1);
        return str1;
    }

    public static final String toHex(byte[] data, int off, int length) {
        // double size, two bytes (hex range) for one byte
        StringBuffer buf = new StringBuffer(data.length * 2);
        for (int i = off; i < length; i++) {
            // don't forget the second hex digit
            if (((int) data[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) data[i] & 0xff, 16));
            if (i < data.length - 1) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }


    protected void onDataReceived(byte[] paramArrayOfByte, int paramInt) {

        String str = getReturnHexString(paramArrayOfByte, paramInt);

        if ((str != null) && (!"".equals(str))) {
            Message localMessage = this.handler.obtainMessage();
            localMessage.what = 2;
            localMessage.obj = str;
            this.handler.sendMessage(localMessage);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mReadThread != null)
            this.isStrart = false;
        try {
            this.mReadThread.join(2000L);
            if (this.mSerialPort != null)
                this.mSerialPort.close();
            this.wm.removeView(this.topBtn);
            this.wm.removeView(this.bottomBtn);
            this.wm.removeView(this.leftBtn);
            this.wm.removeView(this.rightBtn);
            return;
        } catch (InterruptedException localInterruptedException) {
            while (true)
                localInterruptedException.printStackTrace();
        }
    }

}
