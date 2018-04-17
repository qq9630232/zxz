package com.tablet.moran.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 解决listview，scrollview 嵌套滑动冲突的viewpager
 * Created by stone on 2016/3/7.
 */
public class ScrollerViewPager extends ViewPager {



    public ScrollerViewPager(Context context) {
        super(context);
        touch_slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public ScrollerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        touch_slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    /**
     * 触摸时按下的点
     **/
    private PointF downPoint = new PointF();
    /**
     * 触摸时当前的点
     **/
    PointF curPoint = new PointF();
    private int touch_slop;
    private OnSingleTouchListener onSingleTouchListener;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercepted = false;
        curPoint.x = ev.getX();
        curPoint.y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint.x = ev.getX();
                downPoint.y = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = Math.abs(curPoint.x - downPoint.x);
                float y = Math.abs(curPoint.y - downPoint.y);
                intercepted = (y > 1 || x > 1) && y / x > 1;
                if (intercepted)
                    return false;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 每次进行onTouch事件都记录当前的按下的坐标

        curPoint.x = event.getX();
        curPoint.y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下时候的坐标
                downPoint.x = event.getX();
                downPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // if (this.getChildCount() >1) {

                float x = Math.abs(curPoint.x-downPoint.x);
                float y = Math.abs(curPoint.y-downPoint.y);

                if ((y>1||x>1) && x/y>1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }



                // }
                break;
            case MotionEvent.ACTION_UP:
                // 再up时判断是否按下和松手的坐标为一个点
                float absX = Math.abs(downPoint.x - curPoint.x);
                float absY = Math.abs(downPoint.y - curPoint.y);
                if (0<=absX&&absX<=touch_slop && 0<=absY&&absY<=touch_slop) {
                    onSingleTouch(this,getCurrentItem());
                    return true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public void onSingleTouch(View v, int p) {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch(v, p);
        }

    }

    public interface OnSingleTouchListener {
        public void onSingleTouch(View view, int positon);
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int height = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if (h > height)
//                height = h;
//        }
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


}
