package com.tablet.moran.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ASUS on 2016/7/15.
 */
public class FixedViewpager extends ViewPager {

    private boolean scrollble = false;

    public FixedViewpager(Context context) {
        super(context);
    }

    public FixedViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        if (scrollble) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }

    }



    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }
}