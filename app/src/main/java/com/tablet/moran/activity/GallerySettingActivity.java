package com.tablet.moran.activity;

import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.view.CircleMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GallerySettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.menu_anim)
    LinearLayout menuAnim;
    @BindView(R.id.play_menu_15s)
    LinearLayout playMenu15s;
    @BindView(R.id.play_menu_30s)
    LinearLayout playMenu30s;
    @BindView(R.id.play_menu_2m)
    LinearLayout playMenu2m;
    @BindView(R.id.play_menu_10m)
    LinearLayout playMenu10m;
    @BindView(R.id.play_menu_30m)
    LinearLayout playMenu30m;
    @BindView(R.id.circle_menu)
    CircleMenuView circleMenu;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.back_menu_btn)
    LinearLayout backMenuBtn;

    //代表当前为第一层菜单
    private int menu_index = 0;
    //当前需要操作的菜单
    private LinearLayout llMenu;

    private int playTime;//单位：秒


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_setting);
        ButterKnife.bind(this);

        initView();
        setListener();
    }


    @Override
    protected void initView() {
        super.initView();

        AnimationDrawable ad = (AnimationDrawable) menuAnim.getBackground();
        ad.start();

        circleMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleMenu.startAnim();
                llMenu = (LinearLayout) circleMenu.getChildAt(circleMenu.getIndex());
                llMenu.getChildAt(0).setVisibility(View.VISIBLE);
            }
        }, 1000);

        curResId = R.id.back_menu_btn;
    }

    @Override
    protected void setListener() {
        super.setListener();

        backMenuBtn.setOnClickListener(this);
        playMenu15s.setOnClickListener(this);
        playMenu30s.setOnClickListener(this);
        playMenu2m.setOnClickListener(this);
        playMenu10m.setOnClickListener(this);
        playMenu30m.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_menu_btn:
                clickMenu(0);
                finish();
                break;
            case R.id.play_menu_15s:
                playTime = 15;
                clickMenu(1);
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "15" + getResources().getString(R.string.seconds));
                break;
            case R.id.play_menu_30s:
                playTime = 30;
                clickMenu(2);
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "30" + getResources().getString(R.string.seconds));
                break;
            case R.id.play_menu_2m:
                playTime = 2 * 60;
                clickMenu(3);
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "2" + getResources().getString(R.string.minutes));
                break;

            case R.id.play_menu_10m:
                clickMenu(4);
                playTime = 10 * 60;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "10" + getResources().getString(R.string.minutes));
                break;

            case R.id.play_menu_30m:
                clickMenu(5);
                playTime = 30 * 60;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "30" + getResources().getString(R.string.minutes));
                break;
        }
    }

    @Override
    protected void toBlRight() {
        super.toBlRight();
        toBlOk();
    }

    @Override
    protected void toBlOk() {
        super.toBlOk();

        switch (curResId) {
            case R.id.back_menu_btn:
                finish();
                break;
            case R.id.play_menu_15s:
                playTime = 15;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "15" + getResources().getString(R.string.seconds));
                break;
            case R.id.play_menu_30s:
                playTime = 30;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "30" + getResources().getString(R.string.seconds));
                break;
            case R.id.play_menu_2m:
                playTime = 2 * 60;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "2" + getResources().getString(R.string.minutes));
                break;

            case R.id.play_menu_10m:
                playTime = 10 * 60;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "10" + getResources().getString(R.string.minutes));
                break;

            case R.id.play_menu_30m:
                playTime = 30 * 60;
                PreferencesUtils.putString(getApplicationContext(), Constant.PLAY_TIME, playTime + "");
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.play_time_set) + "30" + getResources().getString(R.string.minutes));
                break;

        }
    }

    private void clickMenu(int index) {
        menu_index = circleMenu.getIndex();
        llMenu = (LinearLayout) circleMenu.getChildAt(menu_index);
        llMenu.getChildAt(0).setVisibility(View.GONE);
        circleMenu.endAnim();
        circleMenu.setIndex(index);
        circleMenu.startAnim();
        llMenu = (LinearLayout) circleMenu.getChildAt(circleMenu.getIndex());
        llMenu.getChildAt(0).setVisibility(View.VISIBLE);

        curResId = llMenu.getId();
    }


    @Override
    protected void toBlUp() {
        super.toBlUp();


        menu_index = circleMenu.getIndex();
        llMenu = (LinearLayout) circleMenu.getChildAt(menu_index);
        llMenu.getChildAt(0).setVisibility(View.GONE);
        circleMenu.endAnim();
        circleMenu.setIndex(--menu_index);
        circleMenu.startAnim();
        llMenu = (LinearLayout) circleMenu.getChildAt(circleMenu.getIndex());
        llMenu.getChildAt(0).setVisibility(View.VISIBLE);

        curResId = llMenu.getId();


    }

    @Override
    protected void toBlDown() {
        super.toBlDown();


        menu_index = circleMenu.getIndex();
        llMenu = (LinearLayout) circleMenu.getChildAt(menu_index);
        llMenu.getChildAt(0).setVisibility(View.GONE);
        circleMenu.endAnim();
        circleMenu.setIndex(++menu_index);
        circleMenu.startAnim();
        llMenu = (LinearLayout) circleMenu.getChildAt(circleMenu.getIndex());
        llMenu.getChildAt(0).setVisibility(View.VISIBLE);

        curResId = llMenu.getId();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_gallery_setting);
//            ButterKnife.bind(this);
        circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
        menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
        fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
        backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
        playMenu15s = (LinearLayout) findViewById(R.id.play_menu_15s);
        playMenu30s = (LinearLayout) findViewById(R.id.play_menu_30s);
        playMenu2m = (LinearLayout) findViewById(R.id.play_menu_2m);
        playMenu10m = (LinearLayout) findViewById(R.id.play_menu_10m);
        playMenu30m = (LinearLayout) findViewById(R.id.play_menu_30m);

        playTime = Integer.valueOf(PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME));


        initView();
        setListener();


    }

}
