package com.tablet.moran.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * recycler gird 分隔线
 * Created by stone on 2016/4/7.
 */
public class MyGridItemDecorator extends RecyclerView.ItemDecoration {

    private int mHorizontalSpacing = 5;
    private int mVerticalSpacing = 5;
    private boolean isSetMargin = true;

    public MyGridItemDecorator(int hSpacing, int vSpacing, boolean setMargin) {
        isSetMargin = setMargin;
        mHorizontalSpacing = hSpacing;
        mVerticalSpacing = vSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        boolean isSetMarginLeftAndRight = this.isSetMargin;
        int bottomOffset = mVerticalSpacing;
        int leftOffset = 0;
        int rightOffset = 0;

        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            leftOffset = mHorizontalSpacing;
            rightOffset = mHorizontalSpacing;

        }

        outRect.set(leftOffset, bottomOffset, rightOffset, 0);
//        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
//            outRect.bottom = 0;
//        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
