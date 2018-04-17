package com.tablet.moran.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.DensityUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.zxing.encoding.EncodingHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadAppActivity extends BaseActivity {

    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.msg_tv)
    TextView msgTv;
    @BindView(R.id.barcode_iv)
    ImageView barcodeIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_app);
        ButterKnife.bind(this);

        initView();

        setListener();
    }


    @Override
    protected void initView() {
        super.initView();

        try {
            barcodeIv.setImageBitmap(EncodingHandler.createQRCode("www.baidu.com", DensityUtils.dip2px(230)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setListener() {
        super.setListener();

    }

    @Override
    protected void toBlOk() {
        super.toBlOk();
        finish();
    }

    @Override
    protected void toBlBack() {
        super.toBlBack();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_download_app);
        barcodeIv = (ImageView) findViewById(R.id.barcode_iv);
        initView();
    }
}
