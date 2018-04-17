package com.tablet.moran.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by stone on 2016/6/3.
 */

public class RecyclerScrollerView extends ScrollView {

    int mLastXIntercept = 0;
    int mLastYIntercept = 0;



    public RecyclerScrollerView(Context context) {
        super(context);
    }

    public RecyclerScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                intercepted = false;

                break;
            case MotionEvent.ACTION_MOVE:
                intercepted = false;

                break;
            case MotionEvent.ACTION_UP:

                intercepted = false;
                break;
            default:
                break;
        }

        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }
}
