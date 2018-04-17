package com.tablet.moran.activity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.tablet.moran.MainActivity;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.config.ZxingConfig;
import com.tablet.moran.event.ConnectNetEvent;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ScreenUtils;
import com.tablet.moran.tools.net.NetWorkUtil;
import com.tablet.moran.tools.wifi.WifiConnect;
import com.tablet.moran.view.CircleMenuView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.wifi_setting)
    TextView wifiSetting;
    @BindView(R.id.time_setting)
    TextView timeSetting;
    @BindView(R.id.play_setting)
    TextView playSetting;
    //代表当前为第一层菜单
    private int menu_index = 0;
    private int REQUEST_CODE_SCAN = 111;

    //代表当前为第一个菜单,记录当前的菜单index
    private int menu_item_index = 0;
    //菜单个数
    private int menu_count = 0;
    //当前需要操作的菜单
    private LinearLayout llMenu;

    private int[] resArray = {R.id.sys_menu_btn, R.id.orient_menu_btn, R.id.ar_menu_btn,
            R.id.light_menu_btn, R.id.playtime_menu_btn, R.id.band_menu_btn,
            R.id.download_menu_btn, R.id.wifi_menu_btn, R.id.language_menu_btn,
            R.id.shutdown_menu_btn};


    private WifiManager wifiManager;
    private WifiConnect wifiConnect;


    private String wifiSSD;
    private String time;
    private String playTime;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiConnect = new WifiConnect(wifiManager);

        String diviceId = PushManager.getInstance().getClientid(this);

        initView();

        setListener();

        SLogger.d("<<", "diviceId-->" + diviceId);
        SLogger.d("<<", "H" + "--->" + ScreenUtils.getScrrenHeight(getApplicationContext()) + "-->" + ScreenUtils.getScreenWidth(getApplicationContext()));

        if (!TextUtils.isEmpty(diviceId))
            PreferencesUtils.putString(this, Constant.CLIENT_ID, diviceId);

    }


    Timer timer;

    @Override
    protected void initView() {
        super.initView();

        try {
            final AnimationDrawable ad = (AnimationDrawable) animMenu.getBackground();
            ad.start();

            circleMenu.postDelayed(new Runnable() {
                @Override
                public void run() {

                    circleMenu.startAnim();
                    llMenu = (LinearLayout) circleMenu.getChildAt(circleMenu.getIndex());
                    llMenu.getChildAt(0).setVisibility(View.VISIBLE);
                }
            }, 1000);

            menu_count = circleMenu.getChildCount();


            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (!connected) {
                wifiSetting.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.no_wifi), null, null, null);
                wifiSetting.setText(getResources().getString(R.string.disconnect));
            } else {
                wifiSetting.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.wifi_png), null, null, null);
                wifiSetting.setText(wifiInfo.getSSID());
            }

            playTime = PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME, Constant.DEFAUT_PLAY_TIME);
            int playTimeTemp = Integer.valueOf(playTime);
            if (playTimeTemp > 60) {
                playSetting.setText(getResources().getString(R.string.play_time_set) + playTimeTemp / 60 + getResources().getString(R.string.minutes));
            } else {
                playSetting.setText(getResources().getString(R.string.play_time_set) + playTimeTemp + getResources().getString(R.string.seconds));
            }

            try {
                if (timer == null) {
                    timer = new Timer();
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                Date curDate = new Date(System.currentTimeMillis());
                                timeSetting.setText(formatter.format(curDate).split(" ")[1].substring(0, 5));
                            }
                        });

                    }
                }, 0, 60000);

            } catch (Exception e) {
                e.printStackTrace();
            }

            curResId = R.id.sys_menu_btn;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setListener() {
        super.setListener();

        arMenuBtn.setOnClickListener(this);
        sysMenuBtn.setOnClickListener(this);
        orientMenuBtn.setOnClickListener(this);
        lightMenuBtn.setOnClickListener(this);
        playtimeMenuBtn.setOnClickListener(this);
        bandMenuBtn.setOnClickListener(this);
        downloadMenuBtn.setOnClickListener(this);
        wifiMenuBtn.setOnClickListener(this);
        languageMenuBtn.setOnClickListener(this);
//        timeMenuBtn.setOnClickListener(this);
        shutdownMenuBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_menu_btn:
                if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.LAND) {
                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.unavailable));

                } else {
                    startActivity(new Intent(this, ARActivity.class));

                }
                break;
            case R.id.sys_menu_btn:
                startActivity(new Intent(this, SystemSettingActivity.class));
                break;
            case R.id.orient_menu_btn:

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    PreferencesUtils.putInt(getApplicationContext(), Constant.ORIENT, OrientEvent.POR);
                    int a = PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT);
                    SLogger.d("ori", "------->" + a);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    PreferencesUtils.putInt(getApplicationContext(), Constant.ORIENT, OrientEvent.LAND);
                    int b = PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT);
                    SLogger.d("ori", "------->" + b);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            case R.id.light_menu_btn:
                startActivity(new Intent(this, LightSettingActivity.class));
                break;
            case R.id.playtime_menu_btn:
                startActivity(new Intent(this, GallerySettingActivity.class));
                break;
            case R.id.band_menu_btn:
                startActivity(new Intent(this, BandPhoneActivity.class));
                break;
            case R.id.download_menu_btn:
                AppUtils.showToast(getApplicationContext(), "暂未开放");
                break;
            case R.id.wifi_menu_btn:
//                if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.LAND) {
//                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.unavailable));
//
//                } else {
//                startActivity(new Intent(this, ConnectWifiActivity.class));
                AndPermission.with(this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(SettingActivity.this, ConnectWifiActivity.class);

                                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                                 * 也可以不传这个参数
                                 * 不传的话  默认都为默认不震动  其他都为true
                                 * */

                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);
                                config.setShake(true);
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);

                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

//                                Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
//                }
                break;
            case R.id.language_menu_btn:
                startActivity(new Intent(this, LanguageActivity.class));
                break;
//            case R.id.time_menu_btn:
//                AppUtils.showToast(getApplicationContext(), "暂未开放");
//                break;
            case R.id.shutdown_menu_btn:
                startActivity(new Intent(this, ShutdownSettingActivity.class));
                break;

        }
    }

    @Override
    protected void toBlUp() {
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
    protected void toBlRight() {
        super.toBlRight();
        toBlOk();
    }

    @Override
    protected void onResume() {
        super.onResume();

        playTime = PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME, Constant.DEFAUT_PLAY_TIME);
        int playTimeTemp = Integer.valueOf(playTime);
        if (playTimeTemp > 60) {
            playSetting.setText(getResources().getString(R.string.play_time_set) + playTimeTemp / 60 + getResources().getString(R.string.minutes));
        } else {
            playSetting.setText(getResources().getString(R.string.play_time_set) + playTimeTemp / 60 + getResources().getString(R.string.seconds));
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        connected = NetWorkUtil.isNetworkConnected(this);
        if (!connected) {
            wifiSetting.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.no_wifi), null, null, null);
            wifiSetting.setText(getResources().getString(R.string.disconnect));
        } else {
            wifiSetting.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.wifi_png), null, null, null);
            wifiSetting.setText(wifiInfo.getSSID());
        }

    }

    @Override
    protected void toBlOk() {
        super.toBlOk();
        switch (curResId) {
            case R.id.ar_menu_btn:
                if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.LAND) {
                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.unavailable));

                } else {
                    startActivity(new Intent(this, ARActivity.class));

                }
                break;
            case R.id.sys_menu_btn:
                startActivity(new Intent(this, SystemSettingActivity.class));
                break;
            case R.id.orient_menu_btn:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    PreferencesUtils.putInt(getApplicationContext(), Constant.ORIENT, OrientEvent.POR);
                    int a = PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT);
                    SLogger.d("ori", "------->" + a);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    PreferencesUtils.putInt(getApplicationContext(), Constant.ORIENT, OrientEvent.LAND);
                    int b = PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT);
                    SLogger.d("ori", "------->" + b);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
            case R.id.light_menu_btn:
                startActivity(new Intent(this, LightSettingActivity.class));
                break;
            case R.id.playtime_menu_btn:
                startActivity(new Intent(this, GallerySettingActivity.class));
                break;
            case R.id.band_menu_btn:
                startActivity(new Intent(this, BandPhoneActivity.class));
                break;
            case R.id.download_menu_btn:
                startActivity(new Intent(this, DownloadAppActivity.class));
                break;
            case R.id.wifi_menu_btn:
//                if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.LAND) {
//                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.unavailable));
//
//                } else {
                startActivity(new Intent(this, ConnectWifiActivity.class));

//                }
                break;
            case R.id.language_menu_btn:
                startActivity(new Intent(this, LanguageActivity.class));
                break;
//            case R.id.time_menu_btn:
//                AppUtils.showToast(getApplicationContext(), "暂未开放");
//                break;
            case R.id.shutdown_menu_btn:
                startActivity(new Intent(this, ShutdownSettingActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    /**
     * 暂时用来测试
     *
     * @param event
     */
    public void onEventMainThread(ConnectNetEvent event) {

        connected = NetWorkUtil.isNetworkConnected(this);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (!connected) {
            wifiSetting.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.no_wifi), null, null, null);
            wifiSetting.setText(getResources().getString(R.string.disconnect));
        } else {
            wifiSetting.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.wifi_png), null, null, null);
            wifiSetting.setText(wifiInfo.getSSID());
        }
    }


    BluetoothSocket socket;
    int mAutoConnectInterruptTime;
    boolean isConnect;
    InputStream inputStream;
    OutputStream outputStream;

    @BindView(R.id.circle_menu)
    CircleMenuView circleMenu;

    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.root_coordinator_layout)
    CoordinatorLayout rootCoordinatorLayout;
    @BindView(R.id.menu_anim)
    LinearLayout animMenu;
    @BindView(R.id.ar_menu_btn)
    LinearLayout arMenuBtn;
    @BindView(R.id.sys_menu_btn)
    LinearLayout sysMenuBtn;
    @BindView(R.id.orient_menu_btn)
    LinearLayout orientMenuBtn;
    @BindView(R.id.light_menu_btn)
    LinearLayout lightMenuBtn;
    @BindView(R.id.playtime_menu_btn)
    LinearLayout playtimeMenuBtn;
    @BindView(R.id.band_menu_btn)
    LinearLayout bandMenuBtn;
    @BindView(R.id.download_menu_btn)
    LinearLayout downloadMenuBtn;
    @BindView(R.id.wifi_menu_btn)
    LinearLayout wifiMenuBtn;
    @BindView(R.id.language_menu_btn)
    LinearLayout languageMenuBtn;
    //    @BindView(R.id.time_menu_btn)
//    LinearLayout timeMenuBtn;
    @BindView(R.id.shutdown_menu_btn)
    LinearLayout shutdownMenuBtn;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_setting);
//            ButterKnife.bind(this);
            circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
            fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
            rootCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_coordinator_layout);
            animMenu = (LinearLayout) findViewById(R.id.menu_anim);
            arMenuBtn = (LinearLayout) findViewById(R.id.ar_menu_btn);
            sysMenuBtn = (LinearLayout) findViewById(R.id.sys_menu_btn);
            orientMenuBtn = (LinearLayout) findViewById(R.id.orient_menu_btn);
            lightMenuBtn = (LinearLayout) findViewById(R.id.light_menu_btn);
            playtimeMenuBtn = (LinearLayout) findViewById(R.id.playtime_menu_btn);
            bandMenuBtn = (LinearLayout) findViewById(R.id.band_menu_btn);
            downloadMenuBtn = (LinearLayout) findViewById(R.id.download_menu_btn);
            wifiMenuBtn = (LinearLayout) findViewById(R.id.wifi_menu_btn);
            languageMenuBtn = (LinearLayout) findViewById(R.id.language_menu_btn);
//            timeMenuBtn = (LinearLayout) findViewById(R.id.time_menu_btn);
            shutdownMenuBtn = (LinearLayout) findViewById(R.id.shutdown_menu_btn);
            wifiSetting = (TextView) findViewById(R.id.wifi_setting);
            timeSetting = (TextView) findViewById(R.id.time_setting);
            playSetting = (TextView) findViewById(R.id.play_setting);

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_setting);

            circleMenu = (CircleMenuView) findViewById(R.id.circle_menu);
            fragmentContent = (FrameLayout) findViewById(R.id.fragment_content);
            rootCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_coordinator_layout);
            animMenu = (LinearLayout) findViewById(R.id.menu_anim);
            arMenuBtn = (LinearLayout) findViewById(R.id.ar_menu_btn);
            sysMenuBtn = (LinearLayout) findViewById(R.id.sys_menu_btn);
            orientMenuBtn = (LinearLayout) findViewById(R.id.orient_menu_btn);
            lightMenuBtn = (LinearLayout) findViewById(R.id.light_menu_btn);
            playtimeMenuBtn = (LinearLayout) findViewById(R.id.playtime_menu_btn);
            bandMenuBtn = (LinearLayout) findViewById(R.id.band_menu_btn);
            downloadMenuBtn = (LinearLayout) findViewById(R.id.download_menu_btn);
            wifiMenuBtn = (LinearLayout) findViewById(R.id.wifi_menu_btn);
            languageMenuBtn = (LinearLayout) findViewById(R.id.language_menu_btn);
//            timeMenuBtn = (LinearLayout) findViewById(R.id.time_menu_btn);
            shutdownMenuBtn = (LinearLayout) findViewById(R.id.shutdown_menu_btn);
            wifiSetting = (TextView) findViewById(R.id.wifi_setting);
            timeSetting = (TextView) findViewById(R.id.time_setting);
            playSetting = (TextView) findViewById(R.id.play_setting);

            initView();
            setListener();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                result.setText("扫描结果为：" + content);
                Log.e("zxz",content+"");
            }
        }
    }
}


