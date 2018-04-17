package com.tablet.moran.tools;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Stone on 2017/12/13.
 */

public class LightSensorManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "<<";

    private static LightSensorManager instance;
    private SensorManager mSensorManager;
    private LightSensorListener mLightSensorListener;
    private boolean mHasStarted = false;
    private Context context;

    private LightSensorManager() {
    }

    public static LightSensorManager getInstance() {
        if (instance == null) {
            instance = new LightSensorManager();
        }
        return instance;
    }

    public void start(Context context) {
        this.context = context;
        if (mHasStarted) {
            return;
        }
        mHasStarted = true;
        mSensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // 获取光线传感器
        if (lightSensor != null) { // 光线传感器存在时
            mLightSensorListener = new LightSensorListener();
            mSensorManager.registerListener(mLightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL); // 注册事件监听
        }
    }


    public void stop() {
        if (!mHasStarted || mSensorManager == null) {
            return;
        }
        mHasStarted = false;
        mSensorManager.unregisterListener(mLightSensorListener);
    }

    /**
     * 获取光线强度
     */
    public float getLux() {
        if (mLightSensorListener != null) {
            return mLightSensorListener.lux;
        }
        return -1.0f; // 默认返回-1，表示设备无光线传感器或者为调用start()方法
    }

    private class LightSensorListener implements SensorEventListener {

        private float lux; // 光线强度

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        //---10----10240
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                // 获取光线强度
                lux = event.values[0];
                int LOW = 100;
                int OPEN = 100;
                float spe = 0.5f;

//                SLogger.d(TAG, "lux : " + lux);

 /*               //灵敏度高
                if (PreferencesUtils.getInt(context, LightSettingActivity.SENSOR_SETTING) == LightSettingActivity.FAST) {

                    LOW = 30;
                    OPEN = 5;

                    if (lux < 5) {
                        spe = 3 / 255f;
                    } else if (lux >= 5 && lux < 15) {
                        spe = 12 / 255f;
                    } else if (lux >= 15 && lux < 30) {
                        spe = 20 / 255f;
                    } else if (lux >= 30 && lux < 60) {

                        spe = 50 / 255f;
                    } else if (lux >= 60 && lux < 90) {

                        spe = 75 / 255f;
                    } else if (lux >= 90 && lux < 120) {

                        spe = 100 / 255f;
                    } else if (lux >= 120 && lux < 150) {

                        spe = 131 / 255f;
                    } else if (lux >= 150 && lux < 180) {

                        spe = 157 / 255f;
                    } else if (lux >= 180 && lux < 200) {

                        spe = 180 / 255f;
                    } else if (lux >= 200 && lux < 220) {

                        spe = 203 / 255f;
                    } else if (lux >= 220 && lux < 230) {
                        spe = 230 / 255f;
                    } else if (lux >= 230 && lux < 240) {
                        spe = 240 / 255f;
                    } else {
                        spe = 255 / 255f;
                    }

                    //灵敏度低
                } else if (PreferencesUtils.getInt(context, LightSettingActivity.SENSOR_SETTING) == LightSettingActivity.SLOW) {
                    if (lux < 30) {
                        spe = 30 / 255f;
                    } else if (lux >= 30 && lux < 80) {

                        spe = 60 / 255f;
                    } else if (lux >= 80 && lux < 125) {

                        spe = 100 / 255f;
                    } else if (lux >= 125 && lux < 170) {

                        spe = 155 / 255f;
                    } else if (lux >= 170 && lux < 215) {

                        spe = 190 / 255f;
                    } else {

                        spe = 225 / 255f;
                    }

                    LOW = 5;
                    OPEN = 30;

                    //正常灵敏度
                } else {

                    if (lux < 30) {
                        spe = 20 / 255f;
                    } else if (lux >= 30 && lux < 60) {

                        spe = 40 / 255f;
                    } else if (lux >= 60 && lux < 90) {

                        spe = 60 / 255f;
                    } else if (lux >= 90 && lux < 120) {

                        spe = 85 / 255f;
                    } else if (lux >= 120 && lux < 150) {

                        spe = 108 / 255f;
                    } else if (lux >= 150 && lux < 180) {

                        spe = 132 / 255f;
                    } else if (lux >= 180 && lux < 210) {

                        spe = 160 / 255f;
                    } else if (lux >= 210 && lux <= 230) {

                        spe = 200 / 255f;
                    } else {
                        spe = 230 / 255f;
                    }

                    LOW = 10;
                    OPEN = 10;
                }*/

//                Window window = ((Activity) context).getWindow();
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.screenBrightness = spe;
//                window.setAttributes(lp);

                if ((int) lux <= LOW && lightListener != null) {
                    lightListener.lowLight();
                } else if ((int) lux > OPEN && lightListener != null) {
                    lightListener.openScreen();
                }

            }
        }

    }

    public interface LowLightListener {
        void lowLight();

        void openScreen();
    }

    LowLightListener lightListener;

    public LowLightListener getLightListener() {
        return lightListener;
    }

    public void setLightListener(LowLightListener lightListener) {
        this.lightListener = lightListener;
    }

    public SensorManager getmSensorManager() {
        return mSensorManager;
    }

    public void setmSensorManager(SensorManager mSensorManager) {
        this.mSensorManager = mSensorManager;
    }
}