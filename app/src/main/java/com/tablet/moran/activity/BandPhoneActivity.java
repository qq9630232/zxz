package com.tablet.moran.activity;

import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.igexin.sdk.PushManager;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.model.Device;
import com.tablet.moran.model.Paint;
import com.tablet.moran.presenter.implPresenter.SetNameActivityImpl;
import com.tablet.moran.presenter.implView.ISetPadActivity;
import com.tablet.moran.tools.PreferencesUtils;
import com.zxing.encoding.EncodingHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BandPhoneActivity extends BaseActivity implements ISetPadActivity{

    @BindView(R.id.menu_anim)
    LinearLayout menuAnim;
    @BindView(R.id.back_menu_btn)
    LinearLayout backMenuBtn;
    @BindView(R.id.bar_code_iv)
    ImageView barCodeIv;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;

    SetNameActivityImpl setNameImpl;

    String diviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_phone);
        ButterKnife.bind(this);

        setNameImpl = new SetNameActivityImpl(this, token, this);

        String deviceName = PreferencesUtils.getString(this, Constant.CLIENT_NAME);
        diviceId = PushManager.getInstance().getClientid(this);

        if (!TextUtils.isEmpty(diviceId))
            PreferencesUtils.putString(this, Constant.CLIENT_ID, diviceId);

        Device device = new Device();
        device.setDevice_id(diviceId);
        device.setDevice_name(deviceName);

        setNameImpl.setPadNameBand(device);

        initView();
        setListener();
    }

    @Override
    protected void initView() {
        super.initView();
        AnimationDrawable ad = (AnimationDrawable) menuAnim.getBackground();
        ad.start();

        try {
            barCodeIv.setImageBitmap(EncodingHandler.createQRCode(diviceId, 350));
        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void setListener() {
        super.setListener();
        backMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void toBlOk() {
        super.toBlOk();
        finish();
    }

    @Override
    protected void toBlRight() {
        super.toBlRight();
        toBlOk();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_band_phone);
//            ButterKnife.bind(this);
            menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
            backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
            barCodeIv = (ImageView) findViewById(R.id.bar_code_iv);

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_band_phone);

            menuAnim = (LinearLayout) findViewById(R.id.menu_anim);
            backMenuBtn = (LinearLayout) findViewById(R.id.back_menu_btn);
            barCodeIv = (ImageView) findViewById(R.id.bar_code_iv);

            initView();
            setListener();

        }
    }

    @Override
    public void showProgressDialog() {

        dialog.show();
    }

    @Override
    public void hidProgressDialog() {

        dialog.dismiss();
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void postSuccess(Paint paint) {

    }
}
