package com.tablet.moran.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tablet.moran.R;
import com.tablet.moran.tools.SLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeSettingActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.time_picker)
    TimePicker timePicker;
    @BindView(R.id.back_finish)
    ImageView backFinish;
    @BindView(R.id.finish_btn)
    TextView finishBtn;

    private boolean isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);
        ButterKnife.bind(this);

        initView();
        setListener();
    }

    @Override
    protected void initView() {
        super.initView();

        backBack.setVisibility(View.GONE);
        backFinish.setVisibility(View.VISIBLE);
        curResId = R.id.finish_btn;

        resizePikcer(timePicker);
    }

    @Override
    protected void setListener() {
        super.setListener();

        finishBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    protected void toBlOk() {
        super.toBlOk();

        switch (curResId) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.finish_btn:
                if(!isFinish) {
                    isFinish = true;
                    timePicker.clearFocus();
                    setSysTime();
//                    finish();
                }
                break;
        }
    }

    public void setSysTime(){
        SLogger.d("<<", "-->>>!!!!!!!!!!");
        timePicker.setIs24HourView(true);
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }
//
//    @Override
//    protected void toBlRight() {
//        super.toBlRight();
//        toBlOk();
//    }

    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    /*
         * 调整numberpicker大小
		 */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 30, 0);

        np.setLayoutParams(params);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_time_setting);
//            ButterKnife.bind(this);

            backBack = (ImageView) findViewById(R.id.back_back);
            backBtn = (TextView) findViewById(R.id.back_btn);
            timePicker = (TimePicker) findViewById(R.id.time_picker);
            finishBtn = (TextView) findViewById(R.id.finish_btn);
            backFinish = (ImageView) findViewById(R.id.back_finish);

            initView();
            setListener();


        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_time_setting);
            backBack = (ImageView) findViewById(R.id.back_back);
            backBtn = (TextView) findViewById(R.id.back_btn);
            timePicker = (TimePicker) findViewById(R.id.time_picker);
            finishBtn = (TextView) findViewById(R.id.finish_btn);
            backFinish = (ImageView) findViewById(R.id.back_finish);

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
            case R.id.finish_btn:
                break;
        }
    }
}
