package com.tablet.moran.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.tablet.moran.MainActivity;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.model.Device;
import com.tablet.moran.model.Paint;
import com.tablet.moran.presenter.implPresenter.SetNameActivityImpl;
import com.tablet.moran.presenter.implView.ISetPadActivity;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetPadNameActivity extends BaseActivity implements View.OnClickListener, ISetPadActivity {

    @BindView(R.id.text_name)
    EditText textName;
    @BindView(R.id.later_btn)
    TextView laterBtn;
    @BindView(R.id.finish_btn)
    TextView finishBtn;

    SetNameActivityImpl setNameImpl;
    @BindView(R.id.back_later)
    ImageView backLater;
    @BindView(R.id.back_finish)
    ImageView backFinish;

    private String client_id;

    private int flag = 0;//0:hide  1:show  imm
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pad_name);
        ButterKnife.bind(this);

        setNameImpl = new SetNameActivityImpl(this, token, this);

        client_id = PushManager.getInstance().getClientid(this);

        SLogger.d("<<", "clientId--->>>" + client_id);
        PreferencesUtils.putString(getApplicationContext(), Constant.CLIENT_ID, client_id);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag == 1)
            imm.hideSoftInputFromWindow(textName.getWindowToken(), 0); //强制隐藏键盘
    }

    @Override
    protected void initView() {
        super.initView();

        textName.requestFocus();
        imm.showSoftInput(textName, InputMethodManager.SHOW_FORCED);
        flag = 1;

        backLater.setVisibility(View.INVISIBLE);
        backFinish.setVisibility(View.INVISIBLE);

    }


    @Override
    protected void setListener() {
        super.setListener();

//        laterBtn.setOnClickListener(this);
//        finishBtn.setOnClickListener(this);

        textName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {//EditorInfo.IME_ACTION_SEARCH、EditorInfo.IME_ACTION_SEND等分别对应EditText的imeOptions属性
                    //TODO回车键按下时要执行的操作
                    /*AppUtils.showToast(getApplicationContext(), textName.getText().toString());
                    textName.clearFocus();
                    imm.hideSoftInputFromWindow(textName.getWindowToken(), 0); //强制隐藏键盘
                    backFinish.setVisibility(View.VISIBLE);
                    curResId = R.id.finish_btn;
                    flag = 0;*/
                    String name = textName.getText().toString();
                    if(TextUtils.isEmpty(name)) {

                    } else {
                        Device device = new Device();
                        device.setDevice_name(name);
                        device.setDevice_id(client_id);
                        setNameImpl.setPadName(device);
                    }

                }
                return false;
            }
        });
    }

    /*@Override
    protected void toBlOk() {
        super.toBlOk();
        if(flag == 0) {
            if(curResId == R.id.finish_btn) {
                String name = textName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    Device device = new Device();
                    device.setDevice_name(name);
                    device.setDevice_id(client_id);
                    setNameImpl.setPadName(device);
                } else {
                    AppUtils.showToast(getApplicationContext(), "请输入名称");
                }
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();
        if (flag == 0) {
            if(curResId == R.id.later_btn) {
                curResId = R.id.text_name;
                backFinish.setVisibility(View.INVISIBLE);
                backLater.setVisibility(View.INVISIBLE);
                textName.requestFocus();
                imm.showSoftInput(textName, InputMethodManager.SHOW_FORCED);
                flag = 1;

            } else if(curResId == R.id.finish_btn){
                curResId = R.id.later_btn;
                backLater.setVisibility(View.INVISIBLE);
                backFinish.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void toBlDown() {
        super.toBlDown();
        if (flag == 0) {
            if(curResId == R.id.finish_btn) {
                curResId = R.id.text_name;
                backFinish.setVisibility(View.INVISIBLE);
                backLater.setVisibility(View.INVISIBLE);
                textName.requestFocus();
                imm.showSoftInput(textName, InputMethodManager.SHOW_FORCED);
                flag = 1;

            } else if(curResId == R.id.later_btn){
                curResId = R.id.finish_btn;
                backLater.setVisibility(View.INVISIBLE);
                backFinish.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void toBlRight() {
        super.toBlRight();
        toBlOk();
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.later_btn:
                startActivity(new Intent(this, LiningActivity.class));
                finish();

                break;
            case R.id.finish_btn:
                String name = textName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    Device device = new Device();
                    device.setDevice_name(name);
                    device.setDevice_id(client_id);
                    setNameImpl.setPadName(device);
                } else {
                    AppUtils.showToast(getApplicationContext(), "请输入名称");
                }

                break;
        }
    }

    @Override
    public void showProgressDialog() {
        dialog.show();
    }

    @Override
    public void hidProgressDialog() {
        dialog.hide();
    }

    @Override
    public void showError(String error) {
        dialog.hide();
        AppUtils.showToast(getApplicationContext(), error);
        textName.requestFocus();
        imm.showSoftInput(textName, InputMethodManager.SHOW_FORCED);
    }


    @Override
    public void postSuccess(Paint paint) {

        PreferencesUtils.putString(getApplicationContext(), Constant.CLIENT_NAME, textName.getText().toString());
        AppUtils.showToast(getApplicationContext(), "设置成功");
        startActivity(new Intent(this, LiningActivity.class));
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setContentView(R.layout.activity_set_pad_name);

            textName = (EditText) findViewById(R.id.text_name);
            laterBtn = (TextView) findViewById(R.id.later_btn);
            backFinish = (ImageView) findViewById(R.id.back_finish);
            backLater = (ImageView) findViewById(R.id.back_later);
            finishBtn = (TextView) findViewById(R.id.finish_btn);

            initView();

            setListener();

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setContentView(R.layout.activity_set_pad_name);

            textName = (EditText) findViewById(R.id.text_name);
            laterBtn = (TextView) findViewById(R.id.later_btn);
            finishBtn = (TextView) findViewById(R.id.finish_btn);
            backFinish = (ImageView) findViewById(R.id.back_finish);
            backLater = (ImageView) findViewById(R.id.back_later);

            initView();

            setListener();

        }
    }


}
