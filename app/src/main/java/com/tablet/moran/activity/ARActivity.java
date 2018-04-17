package com.tablet.moran.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.tablet.moran.R;
import com.tablet.moran.UnityPlayerActivity;

public class ARActivity extends BaseActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        startActivity(new Intent(this, UnityPlayerActivity.class));

    }
}
