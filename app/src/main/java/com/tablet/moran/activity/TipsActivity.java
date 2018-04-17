package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.model.TipsBack;
import com.tablet.moran.tools.DensityUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ThreadPoolManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TipsActivity extends BaseActivity {

    public static final String POSITION = "position";

    @BindView(R.id.tip_LL1)
    FrameLayout tipLL1;
    @BindView(R.id.tip_LL2)
    FrameLayout tipLL2;
    //    @BindView(R.id.tip_LL3)
//    FrameLayout tipLL3;
    @BindView(R.id.tip_LL5)
    FrameLayout tipLL5;
    @BindView(R.id.tip_LL4)
    FrameLayout tipLL4;
    @BindView(R.id.pic_bg_root)
    CoordinatorLayout picBgRoot;
//    @BindView(R.id.tip_LL6)
//    FrameLayout tipLL6;
//    @BindView(R.id.tip_LL7)
//    FrameLayout tipLL7;
//    @BindView(R.id.tip_LL8)
//    FrameLayout tipLL8;
//    @BindView(R.id.tip_LL9)
//    FrameLayout tipLL9;

    private TipsBack tipsBack;
    private View tipView;
    private TextView tipTV;
    private FrameLayout tipFL;
    private FrameLayout.LayoutParams fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        ButterKnife.bind(this);

        tipsBack = (TipsBack) getIntent().getSerializableExtra(POSITION);

        initView();
        setListener();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tipsBack = (TipsBack) intent.getSerializableExtra(POSITION);

        SLogger.d("<<", "texture-->>" + tipsBack.getTips_texure());

        initView();

        setListener();
    }

    @Override
    protected void initView() {
        super.initView();


        String bgUrl = PreferencesUtils.getString(getApplicationContext(), Constant.PIC_URL);

        SLogger.d("<<", "tip bg url-->>" + bgUrl);

        if (TextUtils.isEmpty(bgUrl)) {
            picBgRoot.setBackground(getResources().getDrawable(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? R.mipmap.splash_bg_land : R.mipmap.splash_bg));
        } else {
            Glide.with(this).load(bgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    picBgRoot.setBackground(new BitmapDrawable(resource));

                }
            });
        }


        //确定是删除标记，并且view不为null
        if (tipsBack.getFlag() == 2)
            finish();

        try {
            tipLL1 = (FrameLayout) findViewById(R.id.tip_LL1);
            tipLL2 = (FrameLayout) findViewById(R.id.tip_LL2);
//        tipLL3 = (FrameLayout) findViewById(R.id.tip_LL3);
            tipLL4 = (FrameLayout) findViewById(R.id.tip_LL4);
            tipLL5 = (FrameLayout) findViewById(R.id.tip_LL5);
//        tipLL6 = (FrameLayout) findViewById(R.id.tip_LL6);
//        tipLL7 = (FrameLayout) findViewById(R.id.tip_LL7);
//        tipLL8 = (FrameLayout) findViewById(R.id.tip_LL8);
//        tipLL9 = (FrameLayout) findViewById(R.id.tip_LL9);

            clearChildView();

            tipView = LayoutInflater.from(this).inflate(R.layout.tip_item, null);

            tipTV = (TextView) tipView.findViewById(R.id.tip_content);
            tipFL = (FrameLayout) tipView.findViewById(R.id.tip_FL);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                fl = new FrameLayout.LayoutParams(DensityUtils.dip2px(430), DensityUtils.dip2px(380));
            else
                fl = new FrameLayout.LayoutParams(DensityUtils.dip2px(470), DensityUtils.dip2px(410));
            fl.gravity = Gravity.CENTER;

            SLogger.d("<<", "texture111111111-->>" + tipsBack.getTips_texure());

            switch (tipsBack.getTips_texure()) {
                case 1:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_1));
                    break;
                case 2:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_2));
                    break;
                case 3:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_3));
                    break;
                default:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_1));
                    break;

            }
            tipTV.setText(tipsBack.getTips_content());

            tipView.setLayoutParams(fl);
            switch (tipsBack.getTips_location()) {
                case 1:
                    tipLL1.addView(tipView);
                    break;
                case 2:
                    tipLL2.addView(tipView);
                    break;
//            case 3:
//                tipLL3.addView(tipView);
//                break;
                case 3:
                    tipLL4.addView(tipView);
                    break;
                case 4:
                    tipLL5.addView(tipView);
                    break;
//            case 6:
//                tipLL6.addView(tipView);
//                break;
//            case 7:
//                tipLL7.addView(tipView);
//                break;
//            case 8:
//                tipLL8.addView(tipView);
//                break;
//            case 9:
//                tipLL9.addView(tipView);
//                break;
                default:
                    tipLL1.addView(tipView);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clearChildView() {
        if (tipLL1.getChildCount() != 0) {
            tipLL1.removeAllViews();
        } else if (tipLL2.getChildCount() != 0) {
            tipLL2.removeAllViews();
        } else if (tipLL4.getChildCount() != 0) {
            tipLL4.removeAllViews();
        } else if (tipLL5.getChildCount() != 0) {
            tipLL5.removeAllViews();
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_tips);

        picBgRoot = (CoordinatorLayout) findViewById(R.id.pic_bg_root);

        tipsBack = (TipsBack) intent.getSerializableExtra(POSITION);

        initView();

        setListener();

    }
}
