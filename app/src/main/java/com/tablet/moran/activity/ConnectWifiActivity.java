package com.tablet.moran.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.igexin.sdk.PushManager;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.config.ZxingConfig;
import com.tablet.moran.event.ConnectNetEvent;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.model.Device;
import com.tablet.moran.model.LocalPlayList;
import com.tablet.moran.model.Paint;
import com.tablet.moran.model.PlayPictureBack;
import com.tablet.moran.model.TitleInfo;
import com.tablet.moran.presenter.implPresenter.SetNameActivityImpl;
import com.tablet.moran.presenter.implView.ISetPadActivity;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ThreadPoolManager;
import com.tablet.moran.tools.wifi.WifiConnect;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.DecodeImgCallback;
import com.zxing.decoding.DecodeImgThread;
import com.zxing.decoding.ImageUtil;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class ConnectWifiActivity extends BaseCameraActivity implements View.OnClickListener, SurfaceHolder.Callback, ISetPadActivity {

    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.preview_view)
    SurfaceView previewView;
    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.scan_wifi_FL)
    FrameLayout scanWifiFL;
    @BindView(R.id.root_bg)
    CoordinatorLayout rootBg;
    @BindView(R.id.title_wifi_land)
    TextView titleLand;
    @BindView(R.id.title_wifi)
    TextView titlePor;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    //    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    //-----------wifi连接----------------
    private WifiManager wifiManager;
    private WifiConnect wifiConnect;

    private Timer timer;

    private CameraManager cameraManager;

    String deviceName;
    private ZxingConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SLogger.d("<<", "-->>>>>!!!!!!!!!!!!!!!!!" + PreferencesUtils.getString(this, Constant.CLIENT_NAME));

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
 /*先获取配置信息*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {

            Log.i("config", e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }
        setContentView(R.layout.activity_connect_wifi);
        ButterKnife.bind(this);

        setNameImpl = new SetNameActivityImpl(this, token, this);

        if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {
            rootBg.setBackground(getResources().getDrawable(R.mipmap.splash_bg));
            titlePor.setVisibility(View.VISIBLE);
            titleLand.setVisibility(View.GONE);

        } else {
            titleLand.setVisibility(View.VISIBLE);
            titlePor.setVisibility(View.GONE);
            rootBg.setBackground(getResources().getDrawable(R.mipmap.splash_bg_land));
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiConnect = new WifiConnect(wifiManager);

        //初始化view，初始化camera manager
//        CameraManager.init(getApplication());
        cameraManager = new CameraManager(getApplication());
        hasSurface = false;
//        inactivityTimer = new InactivityTimer(this);

        timer = new Timer();

        initView();
        setListener();
    }

    @Override
    protected void initView() {
        super.initView();

        backBack.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        super.setListener();
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        resumeScan();
    }


    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    private void resumeScan() {
        //初始化摄像页面
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        //初始化手机振动状态
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    /**
     * 初始化camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet, cameraManager);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            SLogger.d("<<", "--->>create");
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    /**
     * 初始化扫描识别的声音
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    public void onDestroy() {
//        inactivityTimer.shutdown();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }

        cameraManager.stopPreview();
        cameraManager = null;

        surfaceHolder.removeCallback(this);
        timer.cancel();
        super.onDestroy();
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void setViewfinderView(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param
     */
    public void handleDecode(final Result result) {
        scanWifiFL.setVisibility(View.GONE);
        dialog.show();
        dialog.getMsgView().setText(getResources().getString(R.string.wifi_connecting));
        //处理扫描成功的结果
//        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        final Intent intent = getIntent();
        intent.putExtra(Constant.CODED_CONTENT, result.getText());
        //FIXME
        if (resultString.equals("")) {
            Toast.makeText(getApplicationContext(), "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                SLogger.d("<<", "Result:" + resultString);

                String[] strs = resultString.split("&");
                final String ssid = strs[0];
                String password = strs[1];
                int type = Integer.valueOf(strs[2]);
                WifiConnect.WifiCipherType cipherType;
                if (type == 0) {
                    cipherType = WifiConnect.WifiCipherType.WIFICIPHER_NOPASS;
                } else if (type == 1) {
                    cipherType = WifiConnect.WifiCipherType.WIFICIPHER_WEP;
                } else {
                    cipherType = WifiConnect.WifiCipherType.WIFICIPHER_WPA;
                }

                final boolean res = wifiConnect.connect(ssid, password, cipherType);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res) {

                                    SLogger.d("<<", "-->>>连接成功");
                                    EventBus.getDefault().post(new ConnectNetEvent());

                                    deviceName = PreferencesUtils.getString(getApplicationContext(), Constant.CLIENT_NAME);

                                    if (TextUtils.isEmpty(deviceName)) {
                                        deviceName = "C" + System.currentTimeMillis();
                                        String client_id = PushManager.getInstance().getClientid(ConnectWifiActivity.this);
                                        Device device = new Device();
                                        device.setDevice_name(deviceName);
                                        device.setDevice_id(client_id);
                                        setNameImpl.setPadNameWifi(device);
                                    } else {
                                        setNameImpl.getNews();
                                    }
                                    setResult(RESULT_OK, intent);


                                } else {
                                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.user_wrong));
//                resumeScan();
                                }
                            }
                        });
                    }
                }, 3500);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public CaptureActivityHandler getHandler() {
        return handler;
    }

    public void setHandler(CaptureActivityHandler handler) {
        this.handler = handler;
    }


    @Override
    protected void toBlUp() {
        super.toUp();
        backBack.setVisibility(backBack.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (backBack.getVisibility() == View.VISIBLE) {
            curResId = R.id.back_btn;
        }
    }

    @Override
    protected void toBlDown() {
        super.toDown();

        backBack.setVisibility(backBack.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (backBack.getVisibility() == View.VISIBLE) {
            curResId = R.id.back_btn;
        }
    }

    @Override
    protected void toRight() {
        super.toRight();
    }

    @Override
    protected void toLeft() {
        super.toLeft();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

//        commonInit();
    }

    private void commonInit() {
        setContentView(R.layout.activity_connect_wifi);

        backBtn = (TextView) findViewById(R.id.back_btn);
        backBack = (ImageView) findViewById(R.id.back_back);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        previewView = (SurfaceView) findViewById(R.id.preview_view);
        scanWifiFL = (FrameLayout) findViewById(R.id.scan_wifi_FL);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiConnect = new WifiConnect(wifiManager);

        //初始化view，初始化camera manager
//        CameraManager.init(getApplication());
        cameraManager = new CameraManager(getApplication());
        hasSurface = false;

        timer = new Timer();

        initView();

        setListener();

        resumeScan();
    }

    SetNameActivityImpl setNameImpl;

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hidProgressDialog() {

        dialog.dismiss();
    }

    @Override
    public void showError(String error) {
        AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.connected));
        finish();
    }

    @Override
    public void postSuccess(final Paint paint) {

        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {

                PlayPictureBack pb = new PlayPictureBack();
                TitleInfo t = new TitleInfo();
                t.setPaint_id(paint.getPaint_id());
                t.setDetail_url(paint.getTitle_detail_url());
                t.setGauss_img_url(paint.getGauss_img_url());
                t.setPaint_title(paint.getPaint_title());
                t.setNews(true);
                pb.setTitle_info(t);
                pb.setPictures(paint.getPicture_ids());
                pb.setNews(true);
                pb.setPaint_id(paint.getPaint_id());

                LocalPlayList lp = (LocalPlayList) diskLruCacheHelper.getAsSerializable(Constant.LOCAL_ALL_LIST);

                if (lp == null) {
                    lp = new LocalPlayList();
                    PlayPictureBack localPb = JSONObject.parseObject(PreferencesUtils.getString(getApplicationContext(), Constant.LOCAL_LIST), PlayPictureBack.class);
//            PlayPictureBack testPb = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.PAINT_TEST), PlayPictureBack.class);
                    lp.getList().add(localPb);
//            lp.getList().add(testPb);
                    lp.getList().add(pb);
                    diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);
                } else {
                    List<PlayPictureBack> list = lp.getList();
                    boolean isHas = false;
                    for (int i = 0; i < list.size(); i++) {
                        PlayPictureBack p = list.get(i);
                        if (p.getPaint_id() == pb.getPaint_id()) {
                            isHas = true;
                            break;
                        }
                    }
                    if (!isHas) {
                        list.add(pb);
                        lp.setList(list);
                        diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        dialog.dismiss();
                        AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.connected));
                        PreferencesUtils.putString(getApplicationContext(), Constant.CLIENT_NAME, deviceName);
                        finish();
                    }
                });

            }

        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());

            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed() {
//                    Toast.makeText(CaptureActivity.this, "抱歉，解析失败,换个图片试试.", Toast.LENGTH_SHORT).show();
                }
            }).run();


        }
    }
}
