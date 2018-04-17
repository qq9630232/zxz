package com.tablet.moran.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tablet.moran.BuildConfig;
import com.tablet.moran.R;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.FileUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.net.DownloadUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppUpdateActivity extends BaseActivity implements View.OnClickListener {

    private static final String DOWNLOAD_URL = "https://s3.cn-north-1.amazonaws.com.cn/moranapk/app-release";
    ///storage/emulated/0/Android/data/com.tablet.moran/cache/test

    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.cur_version_tv)
    TextView curVersionTv;
    @BindView(R.id.is_update_tv)
    TextView isUpdateTv;
    @BindView(R.id.select_flag_1)
    ImageView selectFlag1;
    @BindView(R.id.yes_btn)
    LinearLayout yesBtn;
    @BindView(R.id.select_flag_2)
    ImageView selectFlag2;
    @BindView(R.id.no_btn)
    LinearLayout noBtn;
    @BindView(R.id.tip_dialog_LL)
    LinearLayout tipDialogLL;
    @BindView(R.id.progress_tv)
    TextView progressTv;

    private boolean isNeedUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);
        ButterKnife.bind(this);

        String s = FileUtils.getDiskCacheDir(this, "test").getAbsolutePath();

        SLogger.d("<<", "--->>" + s);

        initView();

        setListener();
    }

/*
    private void getPermission() {

        if (!isAdminActive) {//这一句一定要有...
            Intent intent = new Intent();
            //指定动作
            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            //指定给那个组件授权
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivity(intent);
        }


    }*/

    @Override
    protected void initView() {
        super.initView();

        isUpdateTv.setVisibility(View.GONE);
        yesBtn.setVisibility(View.GONE);
        noBtn.setVisibility(View.GONE);

        curResId = R.id.back_btn;
        curResIndex = 0;

        dialog.show();
        dialog.getMsgView().setText(getResources().getString(R.string.install));

        String url = DOWNLOAD_URL + getVersionCode(getApplicationContext()) + ".apk";
        DownloadUtil.get().download(url, "Download", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {

                //取消设备管理器

                devicePolicyManager.removeActiveAdmin(componentName);

                File f = new File(DownloadUtil.get().getCurPath());

                execCommand("system/bin/pm install -r " + f, f);

//                installAPK(f);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                    }
//                });
            }

            @Override
            public void onDownloading(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressTv.setText(String.valueOf(progress) + "%");
                    }
                });
            }

            @Override
            public void onDownloadFailed() {
                dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        isUpdateTv.setVisibility(View.VISIBLE);

                        curVersionTv.setText(getResources().getString(R.string.cur_ver) + AppUtils.getAppVersionName(getApplication()));
                        isUpdateTv.setText(getResources().getString(R.string.cur_ver_ed));
                    }
                });

            }
        });

/*        File file = new File(Environment.getExternalStorageDirectory(), "download/app-release.apk");

        installAPK(file);*/

    }

    File f = new File(Environment.getExternalStorageDirectory().getPath()
            + "/Download");
    String path = Environment.getExternalStorageDirectory().getPath()
            + "/Download";
//    File file;

    public boolean execCommand(String cmd, File file) {
        SLogger.d("SVVV", "进入安装方法");
        Process process = null;
        try {

            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            PackageManager pm = this.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(file.toString(),
                    PackageManager.GET_ACTIVITIES);
            SLogger.d("SVVV++info", info + "");
            ApplicationInfo appInfo = null;
            if (info != null) {
                appInfo = info.applicationInfo;
                String packageName = appInfo.packageName;
                SLogger.d("SVVV+package", packageName);

                Intent intent = getPackageManager().getLaunchIntentForPackage(
                        packageName);
                file.delete();
                startActivity(intent);
                onDestroy();
                System.gc();
            }

            SLogger.d("SVVV", "安装完成");
        } catch (Exception e) {
            return false;
        } finally {
            try {
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(this);
        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);


    }

    /**
     * 安装apk文件
     */
    private void installAPK(File f) {

        // 通过Intent安装APK文件
        Intent intents = new Intent();

        Uri apk;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apk = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", f);
        } else {
            apk = Uri.fromFile(f);
        }

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intents.setAction("android.intent.action.VIEW");
//        intents.addCategory("android.intent.category.DEFAULT");
//        intents.setType("application/vnd.android.package-archive");
//        intents.setData(apk);
        intents.setDataAndType(apk, "application/vnd.android.package-archive");
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intents);

        android.os.Process.killProcess(android.os.Process.myPid());
        // 如果不加上这句的话在apk安装完成之后点击单开会崩溃

    }

    public String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packageInfo.versionCode + 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.yes_btn:

                break;
            case R.id.no_btn:
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
            case R.id.yes_btn:
                if (alterListener != null)
                    alterListener.positiveGo();
                break;
            case R.id.no_btn:
                if (alterListener != null)
                    alterListener.negativeGo();
                break;
            case R.id.back_btn:
                finish();
                break;

        }
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();

        if (!isNeedUpdate)
            return;

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

        if (!isNeedUpdate)
            return;

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_app_update);
//            ButterKnife.bind(this);
            backBtn = (TextView) findViewById(R.id.back_btn);
            backBack = (ImageView) findViewById(R.id.back_back);
            curVersionTv = (TextView) findViewById(R.id.cur_version_tv);
            isUpdateTv = (TextView) findViewById(R.id.is_update_tv);
            yesBtn = (LinearLayout) findViewById(R.id.yes_btn);
            noBtn = (LinearLayout) findViewById(R.id.no_btn);
            selectFlag1 = (ImageView) findViewById(R.id.select_flag_1);
            selectFlag2 = (ImageView) findViewById(R.id.select_flag_2);
            progressTv = (TextView) findViewById(R.id.progress_tv);

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_app_update);

            backBtn = (TextView) findViewById(R.id.back_btn);
            backBack = (ImageView) findViewById(R.id.back_back);
            curVersionTv = (TextView) findViewById(R.id.cur_version_tv);
            isUpdateTv = (TextView) findViewById(R.id.is_update_tv);
            yesBtn = (LinearLayout) findViewById(R.id.yes_btn);
            noBtn = (LinearLayout) findViewById(R.id.no_btn);
            selectFlag1 = (ImageView) findViewById(R.id.select_flag_1);
            selectFlag2 = (ImageView) findViewById(R.id.select_flag_2);
            progressTv = (TextView) findViewById(R.id.progress_tv);

            initView();
            setListener();

        }
    }
}
