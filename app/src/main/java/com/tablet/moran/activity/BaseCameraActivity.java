package com.tablet.moran.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.zxing.Result;
import com.tablet.moran.HHApplication;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.event.SerialEvent;
import com.tablet.moran.presenter.implPresenter.BasePresenterImpl;
import com.tablet.moran.receiver.MyAdmin;
import com.tablet.moran.tools.LightSensorManager;
import com.tablet.moran.tools.MyActivityManager;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.diskCache.DiskLruCacheHelper;
import com.tablet.moran.tools.net.NetWorkUtil;
import com.tablet.moran.view.dialog.CustomProgress;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.view.ViewfinderView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_HOME;
import static android.view.KeyEvent.KEYCODE_MENU;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static com.tablet.moran.activity.BaseActivity.CHINESE;
import static com.tablet.moran.activity.BaseActivity.LANGUAGE;

/**
 * Created by stone on 2016/3/9.
 */
public class BaseCameraActivity extends AppCompatActivity {

    public static final int NORMAL = 0;
    public static final int SLEEP = 1;
    public static final int NIGHT = 2;

    public static final int FAST = 0;
    public static final int MID = 1;
    public static final int SLOW = 2;

    public static final String LIGHT_SETTING = "light_setting";
    public static final String SENSOR_SETTING = "sensor_setting";
    public static final String CLOSE_SCREEN = "close_screen";


    SensorManager mSensorManager;
    PowerManager pm;
    int screenFlag = NORMAL;
    PowerManager.WakeLock localWakeLock;
    boolean close = false;//关灯熄屏 的开关
    int sensorFlag = FAST;
    DevicePolicyManager devicePolicyManager;
    boolean isAdminActive;
    ComponentName componentName;
    protected CoordinatorLayout rootView;


    public CollapsingToolbarLayoutState state;
    private BasePresenterImpl basePresenter;

    public enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }


    protected String token = "";
    protected CustomProgress dialog;
    protected Intent intentGet;
    protected String userId;
    private ViewStub viewStub;

    //popwindow view
    private View popView;
    public PopupWindow popupWindow;
    private LinearLayout wxShare;
    private LinearLayout circleShare;
    private LinearLayout sinaShare;
    private TextView cancel;

    //网络状态
    private ConnectivityManager.NetworkCallback connectivityCallback;
    public boolean connected = true;
    private boolean monitoringConnectivity = false;
    private ConnectivityManager connectivityManager;
    private Unbinder unbinder;

    protected DiskLruCacheHelper diskLruCacheHelper;

    private boolean POR_boolean = false;
    private boolean LAND_boolean = false;

    protected Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hideBottomUIMenu();

        intent = getIntent();

        if(PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                SLogger.d("ori", "-->aaaaaaaaaaaaaaaaaa");

            }
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                SLogger.d("ori", "-->bbbbbbbbbbbbbbbbbb");
            }
        }

        MyActivityManager.getInstance().addActivity(this);

        try{
            diskLruCacheHelper = new DiskLruCacheHelper(this);

        } catch (Exception e) {

        }

        //--------------------------硬件设置亮度 锁屏等--------------------------
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        localWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // 申请权限
        componentName = new ComponentName(this, MyAdmin.class);
        // 判断该组件是否有系统管理员的权限
        isAdminActive = devicePolicyManager
                .isAdminActive(componentName);

        //获取设备管理服务
        //policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        close = PreferencesUtils.getBoolean(getApplicationContext(), CLOSE_SCREEN);
        sensorFlag = PreferencesUtils.getInt(getApplicationContext(), SENSOR_SETTING, FAST);
        screenFlag = PreferencesUtils.getInt(getApplicationContext(), LIGHT_SETTING, NORMAL);
        //---------------------------------------------------------------------


        connected = NetWorkUtil.isNetworkConnected(this);

        if (!connected) {
//            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.net_dissconncted));
        }

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!connected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                connectivityCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {

                        connected = true;

                        initDataSource();
                    }

                    @Override
                    public void onLost(Network network) {
                        connected = false;
                    }
                };
                //监听网络状态
                connectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .build(),
                        connectivityCallback);

                monitoringConnectivity = true;

            }
        }

        token = PreferencesUtils.getString(this, Constant.ACCESS_TOKEN);
        userId = PreferencesUtils.getString(this, Constant.USER_ID);
        dialog = new CustomProgress(this, R.style.AlertDialogStyle);

        //eventbus

        EventBus.getDefault().register(this);
//        dialog.setMessage("     请稍后...");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        this.intent = intent;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void reStartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    //------------------------生命周期-----------------------
    @Override
    protected void onResume() {
        super.onResume();

        isPaused = false;

        LightSensorManager.getInstance().start(this);

        SLogger.d("ori", "------------->" + PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT));
    }


    @Override
    protected void onPause() {
        super.onPause();

        isPaused = true;

        //取消监听网络状态变化

        if (monitoringConnectivity) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                connectivityManager.unregisterNetworkCallback(connectivityCallback);
            }
            monitoringConnectivity = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (basePresenter != null) {
            basePresenter.unsubcrible();
        }

        if (ad != null && ad.isShowing()) {
            ad.dismiss();
        }

        EventBus.getDefault().unregister(this);
        MyActivityManager.getInstance().finishActivity(this);
    }

    /**
     * 验证是否已经登陆
     *
     * @return
     */
    protected boolean goLogin() {
        if (!HHApplication.loginFlag) {
//            startActivity(new Intent(this, WillLoginActivity.class));
            return true;
        } else
            return false;
    }

    //-----友盟分享--------------
    public void initPopWin() {
        popView = LayoutInflater.from(this).inflate(R.layout.pop_share, null);
        popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(popView);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.text_red));
        popupWindow.setBackgroundDrawable(dw);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });

        wxShare = (LinearLayout) popView.findViewById(R.id.wx_share);
        circleShare = (LinearLayout) popView.findViewById(R.id.circle_share);
        sinaShare = (LinearLayout) popView.findViewById(R.id.sina_share);
        cancel = (TextView) popView.findViewById(R.id.cancel_share);
    }


    protected void initDataSource() {

    }

    protected void initView() {

//        rootView = (CoordinatorLayout) findViewById(R.id.pic_bg_root);
//        if(rootView != null) {
//
//            Bitmap b = diskLruCacheHelper.getAsBitmap(Constant.PIC_URL);
//
//
//            rootView.setBackground(new BitmapDrawable(b));
//        }

    }

    protected void setListener() {

        if (viewStub != null && !connected) {

            View v = viewStub.inflate();

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    connected = true;

                    v.setVisibility(View.GONE);

                    initDataSource();
                }
            });
        }

        //TODO 最后写到BASEACTIVITY里面-----------
        LightSensorManager.getInstance().setLightListener(new LightSensorManager.LowLightListener() {
            @Override
            public void lowLight() {
                if (screenFlag == SLEEP && pm.isScreenOn()) {
                    isAdminActive = devicePolicyManager
                            .isAdminActive(componentName);
                    if (isAdminActive)
                        devicePolicyManager.lockNow();
                }

                //关灯息屏
                if (close && pm.isScreenOn()) {
                    isAdminActive = devicePolicyManager
                            .isAdminActive(componentName);
                    if (isAdminActive){
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                devicePolicyManager.lockNow();
                                timer.cancel();
                            }
                        }, 60000);
                    }
                }

            }

            @Override
            public void openScreen() {
                if (close && !pm.isScreenOn()) {

                    if (!localWakeLock.isHeld()) {
                        localWakeLock.acquire();
                    } else {
                        localWakeLock.release();
                    }

                }

            }
        });
    }


    UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调，可以用来处理等待框，或相关的文字提示
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

    protected int getOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? Constant.PORTRAIT : Constant.LANDSCAP;
    }


    protected void toUp() {

    }

    protected void toDown() {

    }

    protected void toRight() {

    }

    protected void toLeft() {

    }

    protected void toOk() {

    }

/*
    private Handler handler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            super.handleMessage(paramAnonymousMessage);
            switch (paramAnonymousMessage.what) {
                case 2:
                    String str = (String) paramAnonymousMessage.obj;
                    SLogger.i("<<", "------------printerInfo::--------------" + str);
                    if (str.indexOf("01") != -1) {
                        toRight();
                        return;
                    } else if (str.indexOf("02") != -1) {
                        toLeft();
                        return;
                    } else if (str.indexOf("04") != -1) {
                        toUp();
                        return;
                    } else {
                        toDown();
                    }
            }

        }
    };*/

    long tempTime;

    protected boolean isPaused;



    /**
     * 暂时用来测试
     *
     * @param event
     */
    public void onEventMainThread(SerialEvent event) {
        String str = event.getMsg();

        if (isPaused)
            return;

        if(PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {
            if (str.contains("01")) {

                SLogger.d("<<", "-->>right");
                toLeft();
            } else if (str.contains("02")) {
                SLogger.d("<<", "-->>left");
                toRight();
            } else if (str.contains("04")) {
                SLogger.d("<<", "-->>up");
                toUp();
            } else if(str.contains("08")){
                SLogger.d("<<", "-->>down");
                toDown();
            }

        } else {
            if (str.contains("01")) {

                SLogger.d("<<", "-->>toDown");
                toDown();
            } else if (str.contains("02")) {
                SLogger.d("<<", "-->>toUp");
                toUp();
            } else if (str.contains("04")) {
                SLogger.d("<<", "-->>toRight");
                toLeft();
            } else if(str.contains("08")){
                SLogger.d("<<", "-->>toLeft");
                toRight();
            }
        }

    }

    public ImageView yesFlag;
    public ImageView noFlag;
    public int curResId;
    public int curResIndex;
    public boolean isDialogShow = false;


    /**
     * 自定义dialog
     *
     * @param message
     * @return
     */
    protected AlertDialog showMyDialog(String message) {
        View v = LayoutInflater.from(this).inflate(R.layout.tip_dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this, R.style.AlertDialogStyle).setView(v);
        LinearLayout pt = (LinearLayout) v.findViewById(R.id.yes_btn);
        TextView msgTv = (TextView) v.findViewById(R.id.tip_msg);
        yesFlag = (ImageView) v.findViewById(R.id.select_flag_1);
        noFlag = (ImageView) v.findViewById(R.id.select_flag_2);
        yesFlag.setVisibility(View.VISIBLE);
        LinearLayout nt = (LinearLayout) v.findViewById(R.id.no_btn);


        msgTv.setText(message);
        pt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alterListener != null) {
                    noFlag.setVisibility(View.INVISIBLE);
                    yesFlag.setVisibility(View.VISIBLE);
                    alterListener.positiveGo();
                }
            }
        });

        nt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alterListener != null) {
                    yesFlag.setVisibility(View.INVISIBLE);
                    noFlag.setVisibility(View.VISIBLE);
                    alterListener.negativeGo();
                }
            }
        });

        ad = ab.create();
        ad.show();
        Button btnPositive =
                ad.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        Button btnNegative =
                ad.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        btnNegative.setVisibility(View.GONE);
        btnPositive.setVisibility(View.GONE);

//        ad.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                switch (keyCode) {
////            case KEYCODE_HOME:
////                break;
//
//                    case KEYCODE_DPAD_DOWN:
//                        toBlDown();
//                        return true;
//
//                    case KEYCODE_DPAD_UP:
//                        toBlUp();
//                        return true;
//
//                    case KEYCODE_DPAD_LEFT:
//                        toBlLeft();
//                        return true;
//
//                    case KEYCODE_DPAD_RIGHT:
//                        toBlRight();
//                        return true;
//
//                    case KEYCODE_ENTER:
//                        toBlOk();
//                        return true;
//
//                }
//                return false;
//            }
//        });

        return ad;
    }

    protected AlterDialogInterface alterListener;
    protected AlertDialog ad;

    public AlterDialogInterface getAlterListener() {
        return alterListener;
    }

    public void setAlterListener(AlterDialogInterface alterListener) {
        this.alterListener = alterListener;
    }

    interface AlterDialogInterface {

        void positiveGo();

        void negativeGo();
    }

    private long curTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if(System.currentTimeMillis() - curTime < 800) {
//            return super.onKeyDown(keyCode, event);
//        }
//        curTime = System.currentTimeMillis();
        switch (keyCode) {
//            case KEYCODE_HOME:
//                break;

            case KEYCODE_HOME:
                toHome();
                break;
            case KEYCODE_BACK:
                toBlBack();
                break;

            case KEYCODE_DPAD_DOWN:
                toBlDown();
                break;

            case KEYCODE_DPAD_UP:
                toBlUp();
                break;

            case KEYCODE_DPAD_LEFT:
                toBlLeft();
                break;

            case KEYCODE_DPAD_RIGHT:
                toBlRight();
                break;

            case KEYCODE_ENTER:
                toBlOk();
                break;

            case KEYCODE_MENU:
                openSetting();
                break;

            case KEYCODE_VOLUME_UP:
                bigVol();
                break;

            case KEYCODE_VOLUME_DOWN:
                smallVol();
                break;

        }


        return super.onKeyDown(keyCode, event);
    }

    protected void toBlBack() {
        finish();
    }

    protected void toBlUp() {

    }

    protected void toBlDown() {

    }

    protected void toBlLeft() {

    }

    protected void toBlRight() {

    }

    protected void toBlOk() {

        if (curResId == R.id.back_btn) {
            finish();
        }
    }

    protected void openSetting() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    protected void bigVol() {

    }

    protected void smallVol() {

    }

    protected void toHome() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PreferencesUtils.putInt(getApplicationContext(), Constant.ORIENT, OrientEvent.LAND);
        } else {
            PreferencesUtils.putInt(getApplicationContext(), Constant.ORIENT, OrientEvent.POR);
        }
    }


    public ViewfinderView getViewfinderView(){
        return null;
    }

    public CaptureActivityHandler getHandler(){
        return null;
    }

    public void handleDecode(Result result, Bitmap barcode){

    }

    /**
     * is chinese
     * @return
     */
    public boolean isChinese() {
        if(PreferencesUtils.getInt(getApplicationContext(), LANGUAGE) == CHINESE) {
            return true;
        } else {
            return false;
        }
    }
}
