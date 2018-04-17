package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tablet.moran.HHApplication;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.FileUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.view.CircleMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.menu_anim)
    LinearLayout menuAnim;
    @BindView(R.id.back_menu_btn)
    LinearLayout backMenuBtn;
    @BindView(R.id.about_menu_btn)
    LinearLayout aboutMenuBtn;
    @BindView(R.id.tip_update_tv)
    ImageView tipUpdateTv;
    @BindView(R.id.update_menu_btn)
    LinearLayout updateMenuBtn;
    @BindView(R.id.reset_menu_btn)
    LinearLayout resetMenuBtn;
    @BindView(R.id.circle_menu)
    CircleMenuView circleMenu;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;

    //代表当前为第一层菜单
    private int menu_index = 0;
    //当前需要操作的菜单
    private LinearLayout llMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
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
        aboutMenuBtn.setOnClickListener(this);
        updateMenuBtn.setOnClickListener(this);
        resetMenuBtn.setOnClickListener(this);

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
            case R.id.about_menu_btn:
                startActivity(new Intent(this, AboutAppActivity.class));
                break;
            case R.id.update_menu_btn:
                startActivity(new Intent(this, AppUpdateActivity.class));
                break;
            case R.id.reset_menu_btn:
                Intent intent = new Intent(this, ResetAppActivity.class);
                intent.putExtra(ResetAppActivity.FLAG, ResetAppActivity.RESET);
                startActivity(intent);
                break;
            case R.id.yes_btn:

                break;
            case R.id.no_btn:
                break;
        }
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();

        if (isDialogShow) {
            yesFlag.setVisibility(yesFlag.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            noFlag.setVisibility(noFlag.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            curResId = yesFlag.getVisibility() == View.VISIBLE ? R.id.yes_btn : R.id.no_btn;
        } else {
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
    }

    @Override
    protected void toBlDown() {
        super.toBlDown();

        if (isDialogShow) {
            yesFlag.setVisibility(yesFlag.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            noFlag.setVisibility(noFlag.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            curResId = yesFlag.getVisibility() == View.VISIBLE ? R.id.yes_btn : R.id.no_btn;
        } else {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_menu_btn:
                finish();
                break;
            case R.id.about_menu_btn:
                startActivity(new Intent(this, AboutAppActivity.class));
                break;
            case R.id.update_menu_btn:
                startActivity(new Intent(this, AppUpdateActivity.class));
                break;
            case R.id.reset_menu_btn:
                Intent intent = new Intent(this, ResetAppActivity.class);
                intent.putExtra(ResetAppActivity.FLAG, ResetAppActivity.RESET);
                startActivity(intent);
                break;
        }
    }


 /*   *//**
     * 重置系统的dialog
     *//*
    private void showUpdateDialog() {

        dialog.show();
        dialog.getMsgView().setText("正在检查更新......");
        //TODO 获取更新的接口 回掉后dialog.dismiss();


        final AlertDialog alertDialog = showMyDialog("是否重置，您缓存的数据将丢失");
        curResId = R.id.yes_btn;
        setAlterListener(new AlterDialogInterface() {
            @Override
            public void positiveGo() {
                alertDialog.dismiss();
                isDialogShow = false;
                curResId = llMenu.getId();

                dialog.show();
                dialog.getMsgView().setText("正在重置");
                PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, HHApplication.LOCAL_LIST_VARIABLE);
                FileUtils.cleanExternalCache(getApplicationContext());
                AppUtils.showToast(getApplicationContext(),"重置完成");
                dialog.dismiss();

            }

            @Override
            public void negativeGo() {
                alertDialog.dismiss();
                isDialogShow = false;
                curResId = llMenu.getId();
            }
        });

        isDialogShow = true;
    }
*/


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_system_setting);
//            ButterKnife.bind(this);
            circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
            menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
            fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
            backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
            aboutMenuBtn = (LinearLayout) findViewById(R.id.about_menu_btn);
            updateMenuBtn = (LinearLayout) findViewById(R.id.update_menu_btn);
            resetMenuBtn = (LinearLayout) findViewById(R.id.reset_menu_btn);

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_system_setting);

            circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
            fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
            menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
            backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
            resetMenuBtn = (LinearLayout) findViewById(R.id.reset_menu_btn);
            aboutMenuBtn = (LinearLayout) findViewById(R.id.about_menu_btn);
            updateMenuBtn = (LinearLayout) findViewById(R.id.update_menu_btn);

            initView();
            setListener();

        }
    }
}
