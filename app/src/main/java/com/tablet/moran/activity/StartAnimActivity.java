package com.tablet.moran.activity;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tablet.moran.R;
import com.tablet.moran.tools.ThreadPoolManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartAnimActivity extends BaseActivity {
    @BindView(R.id.gif_start)
    ImageView gifStart;
    @BindView(R.id.animFL)
    FrameLayout animFL;

//    @BindView(R.id.animFL)
//    FrameLayout animFL;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_anim);
        ButterKnife.bind(this);

        timer = new Timer();
        initView();

        getPermission();
    }


    private void getPermission() {

        if (!isAdminActive) {//这一句一定要有...
            Intent intent = new Intent();
            //指定动作
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            //指定给那个组件授权
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivity(intent);
        }


    }

    @Override
    protected void initView() {
        super.initView();

        Glide.with(this).load(R.mipmap.start_moran).asGif().into(gifStart);

        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    diskLruCacheHelper.delete();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (isAdminActive)
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(StartAnimActivity.this, SplashActivity.class));
                    finish();
                }
            }, 3350);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }
}
