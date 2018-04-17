package com.tablet.moran.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by stone on 2016/3/9.
 */
public class ScrollViewConflict extends ScrollView {

    /** 触摸时按下的点 **/
    private PointF downPoint = new PointF();
    /** 触摸时当前的点 **/
    PointF curPoint = new PointF();

    public ScrollViewConflict(Context context) {
        super(context);
    }

    public ScrollViewConflict(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewConflict(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercepted = false;
        curPoint.x = ev.getX();
        curPoint.y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN :
                downPoint.x = ev.getX();
                downPoint.y = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = Math.abs(curPoint.x-downPoint.x);
                float y = Math.abs(curPoint.y-downPoint.y);
                intercepted = (y>1||x>1) && y/x>1;
                if(intercepted)
                    return true;
                break;

            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
