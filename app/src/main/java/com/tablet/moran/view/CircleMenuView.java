package com.tablet.moran.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tablet.moran.tools.DensityUtils;
import com.tablet.moran.tools.ScreenUtils;

/**
 * Created by ASUS on 2017/10/22.
 * <p>
 * 平板上的圆形菜单view
 */


public class CircleMenuView extends LinearLayout {

    private static final int PORTRAIT = 0;
    private static final int LANDSCAPE = 1;

    private static final int DEVIATION_P = 280;//半径偏移量
    private static final int DEVIATION_L = 130;//半径偏移量

    private int screenW;
    private int screenH;

    //当前item的index
    private int index;

    private Context context;
    //圆的半径
    private int radius;
    private int flag;

    private int num;
    private int cut = 4;

    public CircleMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        screenH = ScreenUtils.getScrrenHeight(context);
        screenW = ScreenUtils.getScreenWidth(context);
        radius = screenH / 2;

        flag = screenH > screenW ? PORTRAIT : LANDSCAPE;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 利用圆的公式计算菜单的位置。
     * (x - w/2)^2 + (y - h/2)^2 = (w/2)^2
     * <p>
     * 利用圆角直接计算xy
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        screenH = ScreenUtils.getScrrenHeight(context);
        screenW = ScreenUtils.getScreenWidth(context);

        flag = screenH > screenW ? PORTRAIT : LANDSCAPE;

        //半径总是等于较小的值
        radius = screenH / 2 - DensityUtils.dip2px(flag == PORTRAIT ? DEVIATION_P : DEVIATION_L);

        int childNum = getChildCount();

        num = childNum;

        double angleUnit = (180 / (childNum + 1)) * Math.PI / 180;

        int halfH = screenH / 2;

        int halfW = screenW / 2;

        for (int i = 0; i < childNum; i++) {

            final View child = getChildAt(i);

            double curAngle = angleUnit * (i + 1);

            int itemW = child.getMeasuredWidth();
            int itemH = child.getMeasuredHeight();

            int left = (int) (screenW - radius * Math.sin(curAngle) - (flag == PORTRAIT ? itemW / 3 : 3*itemW/5));

            int top = (int) (halfH - radius * Math.cos(curAngle) - (flag == PORTRAIT ? itemH : itemH/2));

            int right = left + itemW;

            int bottom = top + itemH;

            if (childNum > 6) {

                if(flag == LANDSCAPE) {
                    if(i == 0) {
                        child.layout(left, top - 38, right, bottom);
                    } else if (i == 1) {
                        child.layout(left, (top), right, bottom);
                    } else if (i == 2) {
                        child.layout(left, (top - 20), right, bottom);
                    } else if (i == 3) {
                        child.layout(left, (top - 40), right, bottom);
                    } else if (i == 4) {
                        child.layout(left, (top - 60), right, bottom);
                    } else if (i == 5) {
                        child.layout(left, (top - 60), right, bottom);
                    } else if (i == 6) {
                        child.layout(left, (top - 40), right, bottom);
                    } else if (i == 7) {
                        child.layout(left, (top - 20), right, bottom);
                    } else if (i == 8) {
                        child.layout(left, (top - 10), right, bottom);
                    } else if (i == 9) {
                        child.layout(left, (top +3), right, bottom);
                    }
                } else {
                    if(i == 0) {
                        child.layout(left, (top - 50), right, bottom);
                    } else if (i == 1) {
                        child.layout(left, (top), right, bottom);
                    } else if (i == 2) {
                        child.layout(left, (top - 20), right, bottom);
                    } else if (i == 3) {
                        child.layout(left, (top - 40), right, bottom);
                    } else if (i == 4) {
                        child.layout(left, (top - 70), right, bottom);
                    } else if (i == 5) {
                        child.layout(left, (top - 90), right, bottom);
                    } else if (i == 6) {
                        child.layout(left, (top - 90), right, bottom);
                    } else if (i == 7) {
                        child.layout(left, (top - 70), right, bottom);
                    } else if (i == 8) {
                        child.layout(left, (top - 30), right, bottom);
                    } else if (i == 9) {
                        child.layout(left, (top+3), right, bottom);
                    }
                }
//                if (i == 0) {
//                    if (flag == LANDSCAPE)
//                        child.layout(left, top - 38, right, bottom);
//                    else
//                        child.layout(left, (top - 70), right, bottom);
//                } else if (i == childNum - 2) {
//                        child.layout(left, (top - 20), right, bottom);
//                } else {
//                    child.layout(left, top, right, bottom);
//                }

            } else {
                child.layout(left, top, right, bottom);
            }


        }
    }

    /**
     * 执行动画效果
     *
     * @return
     */
    public void startAnim() {
        View child = getChildAt(index);

        ObjectAnimator animY = ObjectAnimator//
                .ofFloat(child, "scaleY", 1.0F, 1.2F)//
                .setDuration(500);//
        ObjectAnimator animX = ObjectAnimator//
                .ofFloat(child, "scaleX", 1.0F, 1.2F)//
                .setDuration(500);//
//        int temp = getIndex() + 1;
//        int w = child.getMeasuredHeight();
//        int tW = (cut - (temp % cut))* w / cut;
        if (getIndex() == 0 || getIndex() == num) {
            child.setPivotX(child.getMeasuredWidth() + DensityUtils.dip2px(50));
            child.setPivotY(child.getMeasuredHeight() / 2);
        }
        animY.start();
        animX.start();
    }

    /**
     * 缩小回原型
     */
    public void endAnim() {
        View child = getChildAt(index);

        ObjectAnimator animY = ObjectAnimator//
                .ofFloat(child, "scaleY", 1.2F, 1F)//
                .setDuration(500);//
        ObjectAnimator animX = ObjectAnimator//
                .ofFloat(child, "scaleX", 1.2F, 1F)//
                .setDuration(500);//

        if (getIndex() == 0 || getIndex() == (num - 1)) {
            child.setPivotX(child.getMeasuredWidth() + DensityUtils.dip2px(50));
            child.setPivotY(child.getMeasuredHeight() / 2);
        }


        animY.start();
        animX.start();
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (index >= getChildCount()) {
            index = 0;
        } else if (index < 0) {
            index = getChildCount() - 1;
        }
        this.index = index;
    }

    public int getCut() {
        return cut;
    }

    public void setCut(int cut) {
        this.cut = cut;
    }
}
