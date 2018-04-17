package com.tablet.moran.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.tablet.moran.R;
import com.tablet.moran.tools.DensityUtils;


/**
 * Created by stone on 2016/3/16.
 */

public class GradientProgressbar extends View {

    /**
     * 分段颜色
     */
    private static final int[] SECTION_COLORS = {Color.GREEN, Color.YELLOW, Color.RED};
    /**
     * 进度条最大值
     */
    private float maxCount;
    /**
     * 进度条当前值
     */
    private float currentCount;
    /**
     * 画笔
     */
    private Paint mPaint;
    private int mWidth, mHeight;
    private int mWidth_def, mHeight_def;
    private int curColor = getResources().getColor(R.color.yellow);

    private int colorArr[];
    private String percent;

    public GradientProgressbar(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidth_def = DensityUtils.dip2px(70);
        mHeight_def = mWidth_def;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.circleProgress, defStyleAttr, R.style.CircleProgress);
        mHeight = (int) a.getDimension(R.styleable.circleProgress_circleW, mHeight_def);
        mWidth = mHeight;
        colorArr = getResources().getIntArray(R.array.colorProcess);


        a.recycle();

        initView(context);
    }

    public GradientProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.circleStyle);
//        initView(context);
    }

    public GradientProgressbar(Context context) {
        this(context, null);
//        initView(context);
    }

    private void initView(Context context) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        System.out.println("max=" + maxCount + "  current=" + currentCount);
        //设置画笔颜色
        mPaint.setColor(Color.rgb(71, 76, 80));
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mWidth / 2, mPaint);

        if (percent == null) {
            throw new NullPointerException("please init currentCount firstly");
        }
        //当前的分度值
        mPaint.setColor(curColor);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (mWidth - 20) / 2, mPaint);

        Rect bounds = new Rect();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(40);
        mPaint.getTextBounds(percent, 0, percent.length(), bounds);
        canvas.drawText(percent, getMeasuredWidth() / 2 - bounds.width() / 2, getMeasuredHeight() / 2 + bounds.height() / 2, mPaint);

    }


    /***
     * 设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /***
     * 设置当前的进度值
     *
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        curColor = colorArr[(int) Math.floor(currentCount / maxCount * 10)];
        percent = "" + (int)Math.floor(currentCount / maxCount * 100) + "%";
        invalidate();
    }

    /***
     * 直接设置当前的进度值percent
     *
     * @param currentCount
     */
    public void setCurrentCount(String currentCount) {
//        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
//        curColor = colorArr[(int) Math.floor(currentCount / maxCount * 10)];
        percent = currentCount;
        invalidate();
    }

    public int getCurColor() {
        return curColor;
    }

    public void setCurColor(int curColor) {
        this.curColor = curColor;
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }


}