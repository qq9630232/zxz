package com.tablet.moran.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;

public class TestOrientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_orient);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SLogger.d("ori", "-->1");
            if(PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {
                SLogger.d("ori", "-->11");
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            SLogger.d("ori", "-->2");
            if(PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.LAND) {
                SLogger.d("ori", "-->21");
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }

    }
}
