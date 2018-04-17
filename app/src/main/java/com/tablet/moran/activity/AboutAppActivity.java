package com.tablet.moran.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.PreferencesUtils;

import java.io.File;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutAppActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.edit_name)
    TextView editName;
    @BindView(R.id.all_volume_tv)
    TextView allVolumeTv;
    @BindView(R.id.rest_volume_tv)
    TextView restVolumeTv;
    @BindView(R.id.version_tv)
    TextView versionTv;
    @BindView(R.id.app_name_tv)
    TextView appNameTv;
    @BindView(R.id.serial_num_tv)
    TextView serialNumTv;
    @BindView(R.id.wifi_address_tv)
    TextView wifiAddressTv;
    @BindView(R.id.bt_address_tv)
    TextView btAddressTv;
    @BindView(R.id.tip_dialog_LL)
    LinearLayout tipDialogLL;
    @BindView(R.id.edit_btn)
    TextView editBtn;

    private String allV;
    private String restV;

    WifiManager wifiManager;
    InputMethodManager imm;
    int inputFlag;//0：隐藏 1：显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);

        //获取wifi服务
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
        setListener();
    }

    @Override
    protected void initView() {
        super.initView();

        backBack.setVisibility(View.VISIBLE);
        curResId = R.id.back_btn;
        inputFlag = 0;

        try {
            String name = PreferencesUtils.getString(getApplicationContext(), Constant.CLIENT_NAME);
            editName.setText(name);

            getAllVolume();
            allVolumeTv.setText(allV);
            restVolumeTv.setText(restV);

            versionTv.setText(AppUtils.getAppVersionName(getApplication()));
//            appNameTv.setText("Moran V" + AppUtils.getAppVersionName(getApplication()));
            serialNumTv.setText(PushManager.getInstance().getClientid(this));

            if (connected) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                String ip = intToIp(ipAddress);
                wifiAddressTv.setText(ip);
            } else {
                wifiAddressTv.setText(getResources().getString(R.string.disconnect));
            }

            btAddressTv.setText(BluetoothAdapter.getDefaultAdapter().getAddress());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    private void getAllVolume() {
        String sdcard = Environment.getExternalStorageState();
        //外部储存sdcard存在的情况
        String state = Environment.MEDIA_MOUNTED;
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        if (sdcard.equals(state)) {
            //获得sdcard上 block的总数
            long blockCount = statFs.getBlockCount();
            //获得sdcard上每个block 的大小
            long blockSize = statFs.getBlockSize();
            //计算标准大小使用：1024，当然使用1000也可以
            long bookTotalSize = blockCount * blockSize / 1000 / 1000 / 1000;
            DecimalFormat df = new java.text.DecimalFormat("#.00");
            String t = df.format(bookTotalSize);
            allV = t + "G";

            //获取可供程序使用的Block数量
            long blockavailable = statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal = blockSize * blockavailable / 1000 / 1000 / 1000;
            String a = df.format(blockavailableTotal);
            restV = a + "G";

        } else {
            allV = "32G";
            restV = "25.12G";
        }

    }


    @Override
    protected void setListener() {
        super.setListener();
        backBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();

        if (backBack.getVisibility() == View.VISIBLE) {
            backBack.setVisibility(View.GONE);
            editName.requestFocus();
            imm.showSoftInput(editName, InputMethodManager.SHOW_FORCED);
            inputFlag = 1;
        } else {
            imm.hideSoftInputFromWindow(editName.getWindowToken(), 0); //强制隐藏键盘
            editName.clearFocus();
            backBack.setVisibility(View.VISIBLE);
            curResId = R.id.back_btn;
        }
    }

    @Override
    protected void toBlDown() {
        super.toBlDown();

        if (backBack.getVisibility() == View.VISIBLE) {
            backBack.setVisibility(View.GONE);
            editName.requestFocus();
            imm.showSoftInput(editName, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(editName.getWindowToken(), 0); //强制隐藏键盘
            editName.clearFocus();
            backBack.setVisibility(View.VISIBLE);
            curResId = R.id.back_btn;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0); //强制隐藏键盘
    }

    @Override
    protected void toBlRight() {
        super.toBlRight();
        toBlOk();
    }

    @Override
    protected void toBlOk() {
        super.toBlOk();

        if (curResId == R.id.back_btn) {
            //TODO 如果硬件名称发生改变，上传后在finish
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_about_app);
//            ButterKnife.bind(this);
            backBtn = (TextView) findViewById(R.id.back_btn);
            backBack = (ImageView) findViewById(R.id.back_back);
            editName = (TextView) findViewById(R.id.edit_name);
            allVolumeTv = (TextView) findViewById(R.id.all_volume_tv);
            restVolumeTv = (TextView) findViewById(R.id.rest_volume_tv);
            versionTv = (TextView) findViewById(R.id.version_tv);
            appNameTv = (TextView) findViewById(R.id.app_name_tv);
            serialNumTv = (TextView) findViewById(R.id.serial_num_tv);
            wifiAddressTv = (TextView) findViewById(R.id.wifi_address_tv);
            btAddressTv = (TextView) findViewById(R.id.bt_address_tv);
            editBtn = (TextView) findViewById(R.id.edit_btn);


            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_system_setting);

            backBtn = (TextView) findViewById(R.id.back_btn);
            backBack = (ImageView) findViewById(R.id.back_back);
            editName = (TextView) findViewById(R.id.edit_name);
            allVolumeTv = (TextView) findViewById(R.id.all_volume_tv);
            restVolumeTv = (TextView) findViewById(R.id.rest_volume_tv);
            versionTv = (TextView) findViewById(R.id.version_tv);
            appNameTv = (TextView) findViewById(R.id.app_name_tv);
            serialNumTv = (TextView) findViewById(R.id.serial_num_tv);
            wifiAddressTv = (TextView) findViewById(R.id.wifi_address_tv);
            btAddressTv = (TextView) findViewById(R.id.bt_address_tv);
            editBtn = (TextView) findViewById(R.id.edit_btn);


            initView();
            setListener();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.edit_btn:
                if(backBack.getVisibility() == View.VISIBLE) {
                    backBack.setVisibility(View.GONE);
                    editName.requestFocus();
                    imm.showSoftInput(editName, InputMethodManager.SHOW_FORCED);
                    inputFlag = 1;
                }
                break;
        }
    }
}
