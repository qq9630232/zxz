package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.view.CircleMenuView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LanguageActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.menu_anim)
    LinearLayout menuAnim;
    @BindView(R.id.circle_menu)
    CircleMenuView circleMenu;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.back_menu_btn)
    LinearLayout backMenuBtn;
    @BindView(R.id.chinese_menu_btn)
    LinearLayout chineseMenuBtn;
    @BindView(R.id.english_menu_btn)
    LinearLayout englishMenuBtn;

    //代表当前为第一层菜单
    private int menu_index = 0;
    //当前需要操作的菜单
    private LinearLayout llMenu;

    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
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
        chineseMenuBtn.setOnClickListener(this);
        englishMenuBtn.setOnClickListener(this);


    }

    @Override
    protected void toBlUp() {
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
    protected void toBlOk() {
        super.toBlOk();

        switch (curResId) {
            case R.id.back_menu_btn:
                finish();
                break;

            case R.id.chinese_menu_btn:
                menu_index = 1;
                Intent intent1 = new Intent(this, ResetAppActivity.class);
                intent1.putExtra(Constant.CUR_INDEX, menu_index);
                intent1.putExtra(ResetAppActivity.FLAG, ResetAppActivity.LANGUAGE);
                startActivity(intent1);
                break;
            case R.id.english_menu_btn:

                menu_index = 2;
                Intent intent2 = new Intent(this, ResetAppActivity.class);
                intent2.putExtra(Constant.CUR_INDEX, menu_index);
                intent2.putExtra(ResetAppActivity.FLAG, ResetAppActivity.LANGUAGE);
                startActivity(intent2);
                break;

//            case R.id.yes_btn:
//                if(alterListener != null) {
//                    alterListener.positiveGo();
//                }
//                break;
//
//            case R.id.no_btn:
//                if(alterListener != null) {
//                    alterListener.negativeGo();
//                }
//                break;

        }
    }

    @Override
    protected void toBlRight() {
        super.toBlRight();

        toBlOk();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_menu_btn:
                finish();
                break;

            case R.id.chinese_menu_btn:
                menu_index = 1;
                Intent intent1 = new Intent(this, ResetAppActivity.class);
                intent1.putExtra(Constant.CUR_INDEX, menu_index);
                intent1.putExtra(ResetAppActivity.FLAG, ResetAppActivity.LANGUAGE);
                startActivity(intent1);
                break;
            case R.id.english_menu_btn:

                menu_index = 2;
                Intent intent2 = new Intent(this, ResetAppActivity.class);
                intent2.putExtra(Constant.CUR_INDEX, menu_index);
                intent2.putExtra(ResetAppActivity.FLAG, ResetAppActivity.LANGUAGE);
                startActivity(intent2);
                break;

/*            case R.id.yes_btn:
                if(alterListener != null) {
                    alterListener.positiveGo();
                }
                break;

            case R.id.no_btn:
                if(alterListener != null) {
                    alterListener.negativeGo();
                }
                break;*/

        }
    }


    private void showDialog() {
        final AlertDialog alertDialog = showMyDialog(getResources().getString(R.string.will_restart_app));
        curResId = R.id.yes_btn;
        setAlterListener(new AlterDialogInterface() {
            @Override
            public void positiveGo() {
                if (menu_index == 1) {
                    set(false);
                } else {
                    set(true);
                }

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

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

    public void set(boolean isEnglish) {

        Configuration configuration = getResources().getConfiguration();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (isEnglish) {
            //设置英文
            configuration.locale = Locale.ENGLISH;
        } else {
            //设置中文
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        }
        //更新配置
        getResources().updateConfiguration(configuration, displayMetrics);
    }

    /*private void setLanguage(Locale locale) {
        if (locale == null) {
            return;
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_language);
//            ButterKnife.bind(this);
            circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
            menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
            fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
            backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
            chineseMenuBtn = (LinearLayout) findViewById(R.id.chinese_menu_btn);
            englishMenuBtn = (LinearLayout) findViewById(R.id.english_menu_btn);

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_language);

            circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
            menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
            fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
            backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
            chineseMenuBtn = (LinearLayout) findViewById(R.id.chinese_menu_btn);
            englishMenuBtn = (LinearLayout) findViewById(R.id.english_menu_btn);

            initView();
            setListener();

        }
    }
}
