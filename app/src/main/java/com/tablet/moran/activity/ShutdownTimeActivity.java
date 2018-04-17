package com.tablet.moran.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import com.tablet.moran.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShutdownTimeActivity extends BaseActivity {

    private static final int SHUTDOWN_TIME = 10;

    @BindView(R.id.text_main)
    TextView textMain;
    @BindView(R.id.text_num)
    TextView textNum;

    Timer timer;

    private int time = SHUTDOWN_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown_time);
        ButterKnife.bind(this);

        initView();

        setListener();
    }

    @Override
    protected void initView() {
        super.initView();

        //系统将于10秒后关机
        if (timer == null) {
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    --time;
                    if (time == 0) {
                        timer.cancel();
                        //关机
                        try {
                            Process process = Runtime.getRuntime().exec("su");
                            DataOutputStream out = new DataOutputStream(process.getOutputStream());
                            out.writeBytes("reboot -p\n");
                            out.writeBytes("exit\n");
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textMain.setText(getResources().getString(R.string.sys_will_restart1) + time + getResources().getString(R.string.sys_will_restart2));
                                textNum.setText(String.valueOf(time));
                            }
                        });
                    }

                }
            }, 1000, 1000);
        }

    }

    @Override
    protected void setListener() {
        super.setListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_shutdown_time);

        textMain = (TextView) findViewById(R.id.text_main);
        textNum = (TextView) findViewById(R.id.text_num);

        initView();

        setListener();

    }
}
