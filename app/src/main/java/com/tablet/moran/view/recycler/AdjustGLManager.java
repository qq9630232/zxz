package com.tablet.moran.view.recycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自适应的GridLayoutManager
 *  TODO 如果使用在含有上下padding的父布局中，会出现遮挡item的情况，主要是因为，父布局高度计算未加上底部padding
 *  fix-->:Scrapped or attached views may not be recycled.error   在wrap_content的情况下，不能再手动添加footer，因为计算高度完成了，再添加footer  view还没有isScrap
 * Created by stone on 2016/4/10.
 */
public class AdjustGLManager extends GridLayoutManager {

    private int[] mMeasuredDimension = new int[2];


    public AdjustGLManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AdjustGLManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public AdjustGLManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {

        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        int childCount = getChildCount();
        int spanCount = getSpanCount();

        for (int i = 0; i < getItemCount(); i++) {
            try {
                measureScrapChild(recycler, i,
                        widthSpec,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        mMeasuredDimension);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            if (i % spanCount == 0)
                height += mMeasuredDimension[1];
            if (i < spanCount) {
                width += mMeasuredDimension[0];
//                }
            }
        }
        if(customHeader && getItemCount() % 2 == 0)
            height += mMeasuredDimension[1];
        height += getPaddingBottom();
        height += getPaddingTop();


        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
//                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(widthSize, height);
    }


    private boolean customHeader = false;

    public void setCustomHeader(boolean customHeader){
        this.customHeader = customHeader;

    }


    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        View view = recycler.getViewForPosition(position);

        if (view != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), lp.height);
            view.measure(widthSpec, childHeightSpec);
            measuredDimension[0] = view.getMeasuredWidth();
            measuredDimension[1] = view.getMeasuredHeight()+ lp.bottomMargin + lp.topMargin;
//            measuredDimension[1] = getDecoratedMeasuredHeight(view) + lp.bottomMargin + lp.topMargin;


            recycler.recycleView(view);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        Logger.e("SyLinearLayoutManager state:" + state.toString());
        super.onLayoutChildren(recycler, state);
    }






}
