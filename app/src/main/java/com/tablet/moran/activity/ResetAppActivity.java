package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.FileUtils;
import com.tablet.moran.tools.MyActivityManager;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetAppActivity extends BaseActivity {

    public static final String LANGUAGE = "language";
    public static final String RESET = "reset";
    public static final String FLAG = "flag";
    public static final String DELETE = "delete";
    public static final String DELETEPIC = "delete_pic";

    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.select_flag_1)
    ImageView yesFlag;
    @BindView(R.id.yes_btn)
    LinearLayout yesBtn;
    @BindView(R.id.select_flag_2)
    ImageView noFlag;
    @BindView(R.id.no_btn)
    LinearLayout noBtn;
    @BindView(R.id.tip_dialog_LL)
    LinearLayout tipDialogLL;
    @BindView(R.id.msg_tv)
    TextView msgTv;

    private String flag;
    private int menu_index;//1：chinese       2：english

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_app);
        ButterKnife.bind(this);

        flag = getIntent().getStringExtra(FLAG);
        menu_index = getIntent().getIntExtra(Constant.CUR_INDEX, 1);
        SLogger.d("<<", "language-->" + menu_index);
        initView();
        setListener();
    }

    @Override
    protected void initView() {
        super.initView();
        backBack.setVisibility(View.GONE);
        curResIndex = 1;
        curResId = R.id.yes_btn;
        yesFlag.setVisibility(View.VISIBLE);

        if (flag.equals(LANGUAGE)) {

            msgTv.setText(getResources().getString(R.string.will_restart_app));
        } else if (flag.equals(RESET)) {
            msgTv.setText(getResources().getString(R.string.reset_sys));
        } else if (flag.equals(DELETE)) {
            msgTv.setText(getResources().getString(R.string.delete_paint));
        } else if (flag.equals(DELETEPIC)) {
            msgTv.setText(getResources().getString(R.string.delete_picture));
        }
//        showResetDialog();
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag.equals(LANGUAGE)) {
                    changeLanguage();
                } else if (flag.equals(RESET)) {
                    reset();
                }

            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void changeLanguage() {
        if (menu_index == 1) {
            set(false);
            PreferencesUtils.putInt(getApplicationContext(), LANGUAGE, CHINESE);
        } else {
            set(true);
            PreferencesUtils.putInt(getApplicationContext(), LANGUAGE, ENGLISH);
        }

        MyActivityManager.getInstance().finishAllActivity();
        startActivity(new Intent(this, LiningActivity.class));
//        finish();
//        System.exit(0);
//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
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

/*    private void setLanguage(Locale locale) {
        if (locale == null) {
            return;
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);
    }*/


    private void reset() {
        dialog.show();
        dialog.getMsgView().setText(getResources().getString(R.string.resetting));
//        PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, HHApplication.LOCAL_LIST_VARIABLE);
        FileUtils.cleanExternalCache(getApplicationContext());
        AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.reset_in_progress));

        dialog.dismiss();

        finish();
    }

    private void deletePaint() {
        setResult(RESULT_OK);
        finish();
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
            case R.id.yes_btn:
                if (flag.equals(LANGUAGE)) {
                    changeLanguage();
                } else if (flag.equals(RESET)) {
                    reset();
                } else if (flag.equals(DELETE)) {
                    deletePaint();
                } else if (flag.equals(DELETEPIC)) {
                    deletePaint();
                }
                break;
            case R.id.no_btn:
                finish();
                break;

            case R.id.back_btn:
                finish();
                break;

        }
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();

        curResIndex--;
        curResIndex = curResIndex == -1 ? 2 : curResIndex;

        switch (curResIndex) {
            case 0:
                curResId = R.id.back_btn;
                yesFlag.setVisibility(View.INVISIBLE);
                noFlag.setVisibility(View.INVISIBLE);
                backBack.setVisibility(View.VISIBLE);
                break;
            case 1:
                curResId = R.id.yes_btn;
                yesFlag.setVisibility(View.VISIBLE);
                noFlag.setVisibility(View.INVISIBLE);
                backBack.setVisibility(View.INVISIBLE);
                break;
            case 2:
                curResId = R.id.no_btn;
                yesFlag.setVisibility(View.INVISIBLE);
                noFlag.setVisibility(View.VISIBLE);
                backBack.setVisibility(View.INVISIBLE);
                break;

        }
//
//        yesFlag.setVisibility(yesFlag.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        noFlag.setVisibility(noFlag.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        curResId = yesFlag.getVisibility() == View.VISIBLE ? R.id.yes_btn : R.id.no_btn;

    }

    @Override
    protected void toBlDown() {
        super.toBlDown();

        curResIndex++;
        curResIndex = curResIndex == 3 ? 0 : curResIndex;

        switch (curResIndex) {
            case 0:
                curResId = R.id.back_btn;
                yesFlag.setVisibility(View.INVISIBLE);
                noFlag.setVisibility(View.INVISIBLE);
                backBack.setVisibility(View.VISIBLE);
                break;
            case 1:
                curResId = R.id.yes_btn;
                yesFlag.setVisibility(View.VISIBLE);
                noFlag.setVisibility(View.INVISIBLE);
                backBack.setVisibility(View.INVISIBLE);
                break;
            case 2:
                curResId = R.id.no_btn;
                yesFlag.setVisibility(View.INVISIBLE);
                noFlag.setVisibility(View.VISIBLE);
                backBack.setVisibility(View.INVISIBLE);
                break;

        }

    }

    /**
     * 重置系统的dialog
     */
    private void showResetDialog() {
        final AlertDialog alertDialog = showMyDialog("恢复出厂设置？");
        curResId = R.id.yes_btn;
        setAlterListener(new AlterDialogInterface() {
            @Override
            public void positiveGo() {
                alertDialog.dismiss();


            }

            @Override
            public void negativeGo() {
                alertDialog.dismiss();
                finish();
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_reset_app);
//            ButterKnife.bind(this);
        backBtn = (TextView) findViewById(R.id.back_btn);
        backBack = (ImageView) findViewById(R.id.back_back);
        yesBtn = (LinearLayout) findViewById(R.id.yes_btn);
        noBtn = (LinearLayout) findViewById(R.id.no_btn);
        yesFlag = (ImageView) findViewById(R.id.select_flag_1);
        noFlag = (ImageView) findViewById(R.id.select_flag_2);
        msgTv = (TextView) findViewById(R.id.msg_tv);

        flag = getIntent().getStringExtra(FLAG);
        menu_index = getIntent().getIntExtra(Constant.CUR_INDEX, 1);

        initView();
        setListener();

    }


}
