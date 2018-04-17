package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.tools.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShutdownSettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.min_tv5)
    TextView minTv5;
    @BindView(R.id.min_tv15)
    TextView minTv15;
    @BindView(R.id.min_tv30)
    TextView minTv30;
    @BindView(R.id.min_tv60)
    TextView minTv60;
    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back_5)
    ImageView back5;
    @BindView(R.id.back_15)
    ImageView back15;
    @BindView(R.id.back_30)
    ImageView back30;
    @BindView(R.id.back_60)
    ImageView back60;

    private int curMin;
    private int[] resArray = {R.id.min_tv5, R.id.min_tv15, R.id.min_tv30, R.id.min_tv60, R.id.back_btn};
    private View[] viewArray;
    private View[] backArray;

    private int temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown);
        ButterKnife.bind(this);

        viewArray = new View[]{minTv5, minTv15, minTv30, minTv60, backBtn};
        backArray = new View[]{back5, back15, back30, back60, backBack};

//        goneAllBack();

        initView();
        setListener();
    }

    private void goneAllBack() {
        backBack.setVisibility(View.INVISIBLE);

        for(int j  = 0 ;j < backArray.length; j++) {
            backArray[j].setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < viewArray.length - 1; i++) {
            viewArray[i].setBackground(getResources().getDrawable(R.mipmap.menu_item_unselected));
        }
    }

    private int getCurMin(int index) {
        switch (index) {
            case 0:
                return 5;
            case 1:
                return 15;
            case 2:
                return 30;
            case 3:
                return 60;
            default:
                return 15;
        }
    }

    private void selectByIndex() {
        goneAllBack();
        curResIndex = curResIndex == -1 ? (viewArray.length - 1) : curResIndex;
        curResIndex = curResIndex == viewArray.length ? 0 : curResIndex;

        curMin = getCurMin(curResIndex == (viewArray.length - 1) ? temp : curResIndex);

        if (curResIndex == viewArray.length - 1) {
//            viewArray[temp].setBackground(getResources().getDrawable(R.mipmap.menu_item_selected));
            backBack.setVisibility(View.VISIBLE);
        } else {
//            viewArray[curResIndex].setBackground(getResources().getDrawable(R.mipmap.menu_item_selected));
            backArray[curResIndex].setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initView() {
        super.initView();

        curResIndex = 0;

        selectByIndex();

    }

    @Override
    protected void setListener() {
        super.setListener();

        minTv60.setOnClickListener(this);
        minTv30.setOnClickListener(this);
        minTv15.setOnClickListener(this);
        minTv5.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }


    @Override
    protected void toBlDown() {
        super.toBlDown();
        temp = curResIndex;
        ++curResIndex;

        selectByIndex();
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();
        temp = curResIndex;
        --curResIndex;

        selectByIndex();
    }


    @Override
    protected void toBlRight() {
        super.toBlRight();
        toBlOk();
    }

    @Override
    protected void toBlOk() {
        super.toBlOk();

        if(curResIndex < viewArray.length -1) {

            selectByIndex();
            goAndFinish();
        } else {
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        goneAllBack();
        switch (v.getId()) {
            case R.id.min_tv5:
                curResIndex = 0;
                temp = curResIndex;
                break;
            case R.id.min_tv15:
                curResIndex = 1;
                temp = curResIndex;
                break;
            case R.id.min_tv30:
                curResIndex = 2;
                temp = curResIndex;
                break;
            case R.id.min_tv60:
                curResIndex = 3;
                temp = curResIndex;
                break;

            case R.id.back_btn:
                goneAllBack();
                finish();
                return;
        }
        temp = curResIndex;
        selectByIndex();

        goAndFinish();

    }


    /**
     * 发送定时关机广播，并退出
     */
    private void goAndFinish() {

        if(curMin == 5) {
            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.canceled));

            Intent intent = new Intent();
            intent.setAction(Constant.SHUTDOWN_CANCEL_ACTION);
            sendBroadcast(intent);
            finish();
        } else {
            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.has_setted) + curMin + getResources().getString(R.string.has_setted_min));

            Intent intent = new Intent();
            intent.setAction(Constant.SHUTDOWN_ACTION);
            intent.putExtra(Constant.SHUTDOWN_TIME, curMin);
            sendBroadcast(intent);
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_shutdown);
//            ButterKnife.bind(this);
            minTv5 = (TextView) findViewById(R.id.min_tv5);
            minTv15 = (TextView) findViewById(R.id.min_tv15);
            minTv30 = (TextView) findViewById(R.id.min_tv30);
            minTv60 = (TextView) findViewById(R.id.min_tv60);
            back5 = (ImageView) findViewById(R.id.back_5);
            back15 = (ImageView) findViewById(R.id.back_15);
            back30 = (ImageView) findViewById(R.id.back_30);
            back60 = (ImageView) findViewById(R.id.back_60);
            backBack = (ImageView) findViewById(R.id.back_back);
            backBtn = (TextView) findViewById(R.id.back_btn);

            viewArray = new View[]{minTv5, minTv15, minTv30, minTv60, backBtn};
            backArray = new View[]{back5, back15, back30, back60, backBack};

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_shutdown);

            minTv5 = (TextView) findViewById(R.id.min_tv5);
            minTv15 = (TextView) findViewById(R.id.min_tv15);
            minTv30 = (TextView) findViewById(R.id.min_tv30);
            minTv60 = (TextView) findViewById(R.id.min_tv60);
            back5 = (ImageView) findViewById(R.id.back_5);
            back15 = (ImageView) findViewById(R.id.back_15);
            back30 = (ImageView) findViewById(R.id.back_30);
            back60 = (ImageView) findViewById(R.id.back_60);
            backBack = (ImageView) findViewById(R.id.back_back);
            backBtn = (TextView) findViewById(R.id.back_btn);

            viewArray = new View[]{minTv5, minTv15, minTv30, minTv60, backBtn};
            backArray = new View[]{back5, back15, back30, back60, backBack};


            initView();
            setListener();

        }
    }
}
