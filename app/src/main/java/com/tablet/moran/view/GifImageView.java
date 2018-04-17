package com.tablet.moran.view;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.tablet.moran.R;

/**
 * Created by ASUS on 2016/7/19.
 */
public class GifImageView extends android.support.v7.widget.AppCompatImageView {
    private Context mContext;

    public GifImageView(Context context) {
        super(context);
        mContext = context;
        initView();

    }

    public GifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        Glide.with(mContext).load(R.mipmap.loading_gif).asGif().into(this);
    }
}
