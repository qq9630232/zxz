package com.tablet.moran.activity;

import android.content.ComponentName;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tablet.moran.R;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.LightSensorManager;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ThreadPoolManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LightSettingActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.normal_mode)
    ImageView normalMode;
    @BindView(R.id.sleep_mode)
    ImageView sleepMode;
    @BindView(R.id.night_mode)
    ImageView nightMode;
    //    @BindView(R.id.fast_mode)
//    TextView fastMode;
//    @BindView(R.id.mid_mode)
//    TextView midMode;
//    @BindView(R.id.slow_mode)
//    TextView slowMode;
    @BindView(R.id.open_btn)
    TextView openBtn;
    @BindView(R.id.close_btn)
    TextView closeBtn;


    ComponentName componentName;
    @BindView(R.id.selected_circle_light1)
    ImageView selectedCircleLight1;
    @BindView(R.id.selected_circle_light2)
    ImageView selectedCircleLight2;
    @BindView(R.id.selected_circle_light3)
    ImageView selectedCircleLight3;
    @BindView(R.id.selected_circle_sensor1)
    ImageView selectedCircleSensor1;
    @BindView(R.id.selected_circle_sensor2)
    ImageView selectedCircleSensor2;
    @BindView(R.id.selected_circle_sensor3)
    ImageView selectedCircleSensor3;
    @BindView(R.id.selected_circle_sensor4)
    ImageView selectedCircleSensor4;
    @BindView(R.id.selected_circle_sensor5)
    ImageView selectedCircleSensor5;
    @BindView(R.id.selected_circle_btn1)
    ImageView selectedCircleBtn1;
    @BindView(R.id.selected_circle_btn2)
    ImageView selectedCircleBtn2;
//    DevicePolicyManager policyManager;

    View[] views;

    View[] views1;
    View[] views2;
    View[] views3;
    @BindView(R.id.light_back)
    ImageView lightBack;
    @BindView(R.id.sensor_back)
    ImageView sensorBack;
    @BindView(R.id.close_back)
    ImageView closeBack;

    private int curIndex1;
    private int curIndex2;
    private int curIndex3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_light);
        ButterKnife.bind(this);

        views = new View[]{selectedCircleLight1, selectedCircleLight2, selectedCircleLight3, selectedCircleSensor1, selectedCircleSensor2, selectedCircleSensor3, selectedCircleSensor4, selectedCircleSensor5, selectedCircleBtn1, selectedCircleBtn2, backBack, lightBack, sensorBack, closeBack};
        views1 = new View[]{selectedCircleLight1, selectedCircleLight2, selectedCircleLight3};
        views2 = new View[]{selectedCircleSensor1, selectedCircleSensor2, selectedCircleSensor3, selectedCircleSensor4, selectedCircleSensor5};
        views3 = new View[]{selectedCircleBtn1, selectedCircleBtn2};

//        goneAllView();

        initView();
        setListener();

    }


    private void goneAllView(View[] vs) {
        for (int i = 0; i < vs.length; i++) {
            SLogger.d(">>", "---" + i);
            ImageView v = (ImageView) vs[i];
            v.setVisibility(View.GONE);
        }
//        if (curResIndex != 8)
//            views[curResIndex].setVisibility(View.VISIBLE);
//        else {
//            backBack.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void initView() {
        super.initView();

        goneAllView(views);

        curResIndex = 1;//代表当前在第一个view组
        lightBack.setVisibility(View.VISIBLE);

        switch (screenFlag) {
            case NORMAL:
                selectedCircleLight1.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                selectedCircleLight1.setVisibility(View.VISIBLE);
                curIndex1 = 0;
                break;
            case SLEEP:
                selectedCircleLight2.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                selectedCircleLight2.setVisibility(View.VISIBLE);
                curIndex1 = 1;
                break;
            case NIGHT:
                selectedCircleLight3.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                selectedCircleLight3.setVisibility(View.VISIBLE);
                curIndex1 = 2;
                break;
        }

        switch (sensorFlag) {
            case MODE1:
                selectedCircleSensor1.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                selectedCircleSensor1.setVisibility(View.VISIBLE);
                curIndex2 = 0;
                break;
            case MODE2:
                selectedCircleSensor2.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                selectedCircleSensor2.setVisibility(View.VISIBLE);
                curIndex2 = 1;
                break;
            case MODE3:
                selectedCircleSensor3.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                selectedCircleSensor3.setVisibility(View.VISIBLE);
                curIndex2 = 2;
                break;
            case MODE4:
                selectedCircleSensor4.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                selectedCircleSensor4.setVisibility(View.VISIBLE);
                curIndex2 = 3;
                break;
            case MODE5:
                selectedCircleSensor5.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                selectedCircleSensor5.setVisibility(View.VISIBLE);
                curIndex2 = 4;
                break;
        }

        if (close) {
            selectedCircleBtn1.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
            selectedCircleBtn1.setVisibility(View.VISIBLE);

            curIndex3 = 0;
        } else {
            selectedCircleBtn2.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
            selectedCircleBtn2.setVisibility(View.VISIBLE);
            curIndex3 = 1;
        }

    }

    @Override
    protected void toBlRight() {
        super.toBlRight();

        switch (curResIndex) {
            case 0:
                myFinish();
                break;
            case 1:
                curIndex1++;
                curIndex1 = (curIndex1 == views1.length ? 0 : curIndex1);
//                setLight(curIndex1);
                goneAllView(views1);
                views1[curIndex1].setVisibility(View.VISIBLE);
                ((ImageView) (views1[curIndex1])).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 2:
                curIndex2++;
                curIndex2 = (curIndex2 == views2.length ? 0 : curIndex2);
//                setSensor(curIndex2);
                goneAllView(views2);
                views2[curIndex2].setVisibility(View.VISIBLE);
                ((ImageView) (views2[curIndex2])).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 3:
                curIndex3++;
                curIndex3 = (curIndex3 == views3.length ? 0 : curIndex3);
                setClose(curIndex3);
                goneAllView(views3);
                views3[curIndex3].setVisibility(View.VISIBLE);
                ((ImageView) (views3[curIndex3])).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
        }
    }

    protected void setLight(int index) {
        switch (index) {
            case 0:
                /*Window window1 = this.getWindow();
                WindowManager.LayoutParams lp1 = window1.getAttributes();
                lp1.screenBrightness = 125 / 255f;
                window1.setAttributes(lp1);*/

                screenFlag = NORMAL;
                PreferencesUtils.putInt(getApplicationContext(), LIGHT_SETTING, NORMAL);
                break;
            case 1:
                //睡眠模式  等同于 关灯息屏开启
                close = true;
                PreferencesUtils.putInt(getApplicationContext(), LIGHT_SETTING, SLEEP);
                PreferencesUtils.putBoolean(getApplicationContext(), CLOSE_SCREEN, true);
                break;

            case 2:
                PreferencesUtils.putInt(getApplicationContext(), LIGHT_SETTING, NIGHT);
                screenFlag = NIGHT;
                /*Window window = this.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.screenBrightness = 4 / 255f;
                window.setAttributes(lp);*/
                break;
        }
    }

    private void setSensor(int index) {
        switch (index) {
            case 0:
                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, MODE1);
                break;

            case 1:
                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, MODE2);
                break;

            case 2:
                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, MODE3);
                break;

            case 3:
                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, MODE4);
                break;

            case 4:
                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, MODE5);
                break;
        }
    }

    private void setClose(int index) {
        switch (index) {
            case 0:
                close = true;
                PreferencesUtils.putBoolean(getApplicationContext(), CLOSE_SCREEN, true);
                break;

            case 1:
                close = false;
                PreferencesUtils.putBoolean(getApplicationContext(), CLOSE_SCREEN, false);
                break;
        }
    }

    @Override
    protected void toBlLeft() {
        super.toBlLeft();

        switch (curResIndex) {

            case 1:
                curIndex1--;
                curIndex1 = (curIndex1 == -1 ? (views1.length - 1) : curIndex1);
//                setLight(curIndex1);
                goneAllView(views1);
                views1[curIndex1].setVisibility(View.VISIBLE);
                ((ImageView) (views1[curIndex1])).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 2:
                curIndex2--;
                curIndex2 = (curIndex2 == -1 ? (views2.length - 1) : curIndex2);
//                setSensor(curIndex2);
                goneAllView(views2);
                views2[curIndex2].setVisibility(View.VISIBLE);
                ((ImageView) (views2[curIndex2])).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 3:
                curIndex3--;
                curIndex3 = (curIndex3 == -1 ? (views3.length - 1) : curIndex3);
//                setClose(curIndex3);
                goneAllView(views3);
                views3[curIndex3].setVisibility(View.VISIBLE);
                ((ImageView) (views3[curIndex3])).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
        }
    }

    @Override
    protected void toBlOk() {
        super.toBlOk();
        if (curResIndex == 0) {
            myFinish();
        }
    }

    @Override
    protected void toBlBack() {
        myFinish();
//        super.toBlBack();
    }

    @Override
    protected void toBlDown() {
        super.toBlDown();
        SLogger.d("<<", "当前菜单-->>" + curResIndex);
        switch (curResIndex) {
            case 0:
                backBack.setVisibility(View.GONE);
                lightBack.setVisibility(View.VISIBLE);
                ((ImageView) views1[curIndex1]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 1:
                sensorBack.setVisibility(View.VISIBLE);
                lightBack.setVisibility(View.GONE);
                ((ImageView) views1[curIndex1]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                ((ImageView) views2[curIndex2]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 2:
                closeBack.setVisibility(View.VISIBLE);
                sensorBack.setVisibility(View.GONE);
                ((ImageView) views2[curIndex2]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                ((ImageView) views3[curIndex3]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 3:
                ((ImageView) views3[curIndex3]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                closeBack.setVisibility(View.GONE);
                backBack.setVisibility(View.VISIBLE);
                break;
        }
        curResIndex++;
        curResIndex = (curResIndex == 4 ? 0 : curResIndex);//与整个view纵向组的个数比较

    }

    @Override
    protected void toBlUp() {
        super.toBlUp();
        switch (curResIndex) {
            case 0:
                backBack.setVisibility(View.GONE);
                closeBack.setVisibility(View.VISIBLE);
                ((ImageView) views3[curIndex3]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                break;
            case 1:
                backBack.setVisibility(View.VISIBLE);
                lightBack.setVisibility(View.GONE);
                ((ImageView) views1[curIndex1]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                break;
            case 2:
                sensorBack.setVisibility(View.GONE);
                lightBack.setVisibility(View.VISIBLE);
                ((ImageView) views1[curIndex1]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                ((ImageView) views2[curIndex2]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));

                break;
            case 3:
                sensorBack.setVisibility(View.VISIBLE);
                closeBack.setVisibility(View.GONE);
                ((ImageView) views2[curIndex2]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
                ((ImageView) views3[curIndex3]).setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_white));
                break;
        }
        curResIndex--;
        curResIndex = (curResIndex == -1 ? 3 : curResIndex);//与整个view纵向组的个数比较
    }

    @Override
    protected void setListener() {
        super.setListener();
        backBtn.setOnClickListener(this);
        normalMode.setOnClickListener(this);
        sleepMode.setOnClickListener(this);
        nightMode.setOnClickListener(this);
//        fastMode.setOnClickListener(this);
//        midMode.setOnClickListener(this);
//        slowMode.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        openBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

//        switch (v.getId()) {
//            case R.id.normal_mode:
//                curIndex1 = 0;
//                curResIndex = 1;
//                lightBack.setVisibility(View.VISIBLE);
//                if (screenFlag == NIGHT) {
//                    Window window = this.getWindow();
//                    WindowManager.LayoutParams lp = window.getAttributes();
//                    lp.screenBrightness = 125 / 255f;
//                    window.setAttributes(lp);
//                }
//
//                screenFlag = NORMAL;
//                selectedCircleLight1.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                PreferencesUtils.putInt(getApplicationContext(), LIGHT_SETTING, NORMAL);
//                break;
//            case R.id.sleep_mode:
//                curIndex1 = 1;
//                curResIndex = 1;
//                lightBack.setVisibility(View.VISIBLE);
//                screenFlag = SLEEP;
//                selectedCircleLight2.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                PreferencesUtils.putInt(getApplicationContext(), LIGHT_SETTING, SLEEP);
//                break;
//
//            case R.id.night_mode:
//                curIndex1 = 2;
//                curResIndex = 1;
//                lightBack.setVisibility(View.VISIBLE);
//                selectedCircleLight3.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                PreferencesUtils.putInt(getApplicationContext(), LIGHT_SETTING, NIGHT);
//                screenFlag = NIGHT;
//                Window window = this.getWindow();
//                break;
//
//            case R.id.fast_mode:
//                curIndex2 = 0;
//                curResIndex = 2;
//                sensorBack.setVisibility(View.VISIBLE);
//                selectedCircleSensor1.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, FAST);
////                LightSensorManager.getInstance().getmSensorManager().
//                break;
//
//            case R.id.mid_mode:
//                curIndex2 = 1;
//                curResIndex = 2;
//                selectedCircleSensor2.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                sensorBack.setVisibility(View.VISIBLE);
//                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, MID);
//                break;
//
//            case R.id.slow_mode:
//                curIndex2 = 2;
//                curResIndex = 2;
//                sensorBack.setVisibility(View.VISIBLE);
//                selectedCircleSensor3.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                PreferencesUtils.putInt(getApplicationContext(), SENSOR_SETTING, SLOW);
//                break;
//
//            case R.id.close_btn:
//                curIndex3 = 0;
//                curResIndex = 3;
//                closeBack.setVisibility(View.VISIBLE);
//                selectedCircleBtn1.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                close = false;
//                PreferencesUtils.putBoolean(getApplicationContext(), CLOSE_SCREEN, false);
//                break;
//
//            case R.id.open_btn:
//                curIndex3 = 1;
//                curResIndex = 3;
//                closeBack.setVisibility(View.VISIBLE);
//                selectedCircleBtn2.setImageDrawable(getResources().getDrawable(R.mipmap.select_circle_blue));
//                close = true;
//                PreferencesUtils.putBoolean(getApplicationContext(), CLOSE_SCREEN, true);
//                break;
//            case R.id.back_btn:
//                curResIndex = 0;
//                SLogger.d("<<", "-->>gogoooo");
//                backBack.setVisibility(View.VISIBLE);
//                myFinish();
//                //TODO 使用remove  hide的api，这块最后做
////                pm.goToSleep
////                //屏幕会持续点亮
////                localWakeLock.setReferenceCounted(false);
////
////                localWakeLock.acquire();
//////释放锁，屏幕熄灭。
////                SLogger.d("<<", "-->>gogoooo11111");
////                localWakeLock.release();
////                SLogger.d("<<", "-->>gogoooo22222");
//                SLogger.d("<<", "---------------->" + isAdminActive);
//
//                break;
//        }
//        goneAllView();
//        initView();
    }

    private void myFinish() {

        AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.light_success));
        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLight(curIndex1);
                        setSensor(curIndex2);
                        setClose(curIndex3);
                        finish();
                    }
                });
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_light);

        backBtn = (TextView) findViewById(R.id.back_btn);
        backBack = (ImageView) findViewById(R.id.back_back);
        normalMode = (ImageView) findViewById(R.id.normal_mode);
        sleepMode = (ImageView) findViewById(R.id.sleep_mode);
        nightMode = (ImageView) findViewById(R.id.night_mode);
//            fastMode = (TextView) findViewById(R.id.fast_mode);
//            midMode = (TextView) findViewById(R.id.mid_mode);
//            slowMode = (TextView) findViewById(R.id.slow_mode);
        openBtn = (TextView) findViewById(R.id.open_btn);
        closeBtn = (TextView) findViewById(R.id.close_btn);
        selectedCircleLight1 = (ImageView) findViewById(R.id.selected_circle_light1);
        selectedCircleLight2 = (ImageView) findViewById(R.id.selected_circle_light2);
        selectedCircleLight3 = (ImageView) findViewById(R.id.selected_circle_light3);
        selectedCircleSensor1 = (ImageView) findViewById(R.id.selected_circle_sensor1);
        selectedCircleSensor2 = (ImageView) findViewById(R.id.selected_circle_sensor2);
        selectedCircleSensor3 = (ImageView) findViewById(R.id.selected_circle_sensor3);
        selectedCircleSensor4 = (ImageView) findViewById(R.id.selected_circle_sensor4);
        selectedCircleSensor5 = (ImageView) findViewById(R.id.selected_circle_sensor5);
        selectedCircleBtn1 = (ImageView) findViewById(R.id.selected_circle_btn1);
        selectedCircleBtn2 = (ImageView) findViewById(R.id.selected_circle_btn2);
        lightBack = (ImageView) findViewById(R.id.light_back);
        sensorBack = (ImageView) findViewById(R.id.sensor_back);
        closeBack = (ImageView) findViewById(R.id.close_back);

        views = new View[]{selectedCircleLight1, selectedCircleLight2, selectedCircleLight3, selectedCircleSensor1, selectedCircleSensor2, selectedCircleSensor3, selectedCircleSensor4, selectedCircleSensor5, selectedCircleBtn1, selectedCircleBtn2, backBack, lightBack, sensorBack, closeBack};
        views1 = new View[]{selectedCircleLight1, selectedCircleLight2, selectedCircleLight3};
        views2 = new View[]{selectedCircleSensor1, selectedCircleSensor2, selectedCircleSensor3, selectedCircleSensor4, selectedCircleSensor5};
        views3 = new View[]{selectedCircleBtn1, selectedCircleBtn2};

//            goneAllView();

        initView();

        setListener();

    }


/*    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LightSensorManager.getInstance().stop();

    }*/
}
