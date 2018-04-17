package com.tablet.moran.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.SLogger;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class VideoLandActivity extends BaseVideoActivity {

    private String video_path;
    private JCVideoPlayer videoController;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_video);

        SLogger.d("<<", "-->>>landdddddddddddd");

        video_path = getIntent().getStringExtra(Constant.VIDEO);
        title = getIntent().getStringExtra(Constant.TITLE);

        initView();

        setListener();
    }

    @Override
    protected void initView() {
        super.initView();

//        videoController.setSkin(R.color.colorAccent, R.color.transparent, R.drawable.transparent,
//                R.color.bottom_bg, R.drawable.transparent, R.drawable.transparent);
        videoController = (JCVideoPlayer) findViewById(R.id.video_controller);

        //初始化视频播放
//        videoController.setUpForFullscreen(video_path, title);
//
        videoController.setUpForFullscreen(video_path, title);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog.show();
            dialog.getMsgView().setText("加载中...");

            SLogger.d("<<", "-->>>landdddddddddddd");

            videoController.postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoController.getIvStart().performClick();
                    dialog.dismiss();
                }
            }, 800);

        } else {
            dialog.show();
            dialog.getMsgView().setText("加载中...");
            videoController.postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoController.getIvStart().performClick();
                    dialog.dismiss();
                }
            }, 800);

        }

    }


    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        video_path = intent.getStringExtra(Constant.VIDEO);
        title = getIntent().getStringExtra(Constant.TITLE);

        initView();

        setListener();
    }

    @Override
    protected void toUp() {
        super.toUp();
        finish();
    }

    @Override
    protected void toDown() {
        super.toDown();
        finish();
    }

    @Override
    protected void toLeft() {
        super.toLeft();
        finish();
    }

    @Override
    protected void toRight() {
        super.toRight();
        finish();
    }


    @Override
    protected void toBlSetting() {
        super.toBlSetting();
        finish();
    }
}
