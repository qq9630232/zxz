package com.tablet.moran.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tablet.moran.R;

/**
 * Created by ASUS on 2016/7/20.
 */
public class CustomProgress extends Dialog {

    private Context mContext;

    private TextView msgView;

    public CustomProgress(Context context) {
        this(context, R.style.Custom_Progress);
    }

    public CustomProgress(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View dialogView = View.inflate(mContext, R.layout.loading_dialog, null);

        msgView = (TextView) dialogView.findViewById(R.id.loading_msg);

        setContentView(dialogView);


//        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        llp.setMargins(50,50,50,50);
//        setContentView(dialogView, llp);
    }

    public TextView getMsgView() {
        return msgView;
    }

    public void setMsgView(TextView msgView) {
        this.msgView = msgView;
    }
}
