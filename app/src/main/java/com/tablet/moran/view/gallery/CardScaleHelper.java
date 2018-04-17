package com.tablet.moran.view.gallery;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tablet.moran.tools.DensityUtils;
import com.tablet.moran.tools.ScreenUtils;

/**
 * Created by jameson on 8/30/16.
 */
public class CardScaleHelper {
    private RecyclerView mRecyclerView;
    private Context mContext;

    private static final float MIN_SCALE = 1f;
    private static final float MAX_SCALE = 1.4f;

    private float mScale = 0.9f; // 两边视图scale
    private int mPagePadding = 15; // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
    private int mShowLeftCardWidth = 15;   // 左边卡片显示大小

    private int mCardWidth; // 卡片宽度
    private int mOnePageWidth; // 滑动一页的距离
    private int mCardGalleryWidth;

    private int mCurrentItemPos;
    private int mCurrentItemOffset;

    private int mMinWidth;
    private int mMaxWidth;

    private static final int scrollL = 600;
    private static final int scrollP = 440;

    private int flag = 0;//0:portrait  1:landscape;


    private CardLinearSnapHelper mLinearSnapHelper;

    public void attachToRecyclerView(final RecyclerView mRecyclerView) {
        // 开启log会影响滑动体验, 调试时才开启
        this.mRecyclerView = mRecyclerView;
        mContext = mRecyclerView.getContext();

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            flag = 1;
        }

        mMinWidth = (int) (ScreenUtils.getScreenWidth(mContext) * 0.28f);
        mMaxWidth = ScreenUtils.getScreenWidth(mContext) - 2 * mMinWidth;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mLinearSnapHelper.mNoNeedToScroll = mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(mRecyclerView.getAdapter().getItemCount() - 1);
                    if (stillCallBack != null)
                        stillCallBack.still();
                } else {
                    mLinearSnapHelper.mNoNeedToScroll = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int childCount = recyclerView.getChildCount();
                Log.e("tag", childCount + "");
                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
//                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
//                    lp.rightMargin = 5;
//                    lp.height = DensityUtils.dip2px(200);


                    int left = child.getLeft();
                    int right = ScreenUtils.getScreenWidth(mContext) - child.getRight();
                    final float percent = left < 0 || right < 0 ? 0 : Math.min(left, right) * 1f / Math.max(left, right);
                    Log.e("tag", "percent = " + percent);
                    float scaleFactor = MIN_SCALE + Math.abs(percent) * (MAX_SCALE - MIN_SCALE);
                    int width = (int) (mMinWidth + Math.abs(percent) * (mMaxWidth - mMinWidth));
//                    lp.width = width;
//                    child.setLayoutParams(lp);
                    child.setScaleY(scaleFactor);
                    child.setScaleX(scaleFactor);
                }
            }
        });

//        initWidth();
        mLinearSnapHelper = new CardLinearSnapHelper();
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * 初始化卡片宽度
     */
    private void initWidth() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mCardGalleryWidth = 500;
                mCardWidth = mCardGalleryWidth - DensityUtils.dip2px(2 * (mPagePadding + mShowLeftCardWidth));
                mOnePageWidth = mCardWidth;
                mRecyclerView.smoothScrollToPosition(mCurrentItemPos);
//                onScrolledChangedCallback();
            }
        });
    }

    /**
     * @param currentItemPos
     * @param orient         0: right 1:left
     */
    public void setCurrentItemPos(int currentItemPos, int orient) {
        this.mCurrentItemPos = currentItemPos;

        if (mRecyclerView == null)
            return;
        //portrait
        if (flag == 0) {
            mRecyclerView.smoothScrollBy(DensityUtils.dip2px(orient == 0 ? scrollP : -scrollP), 0);
        } else {
            mRecyclerView.smoothScrollBy(DensityUtils.dip2px(orient == 0 ? scrollL : -scrollL), 0);
        }
    }

    public int getCurrentItemPos() {
        return mCurrentItemPos;
    }

    private int getDestItemOffset(int destPos) {
        return mOnePageWidth * destPos;
    }

    /**
     * 计算mCurrentItemOffset
     */
    private void computeCurrentItemPos() {
        if (mOnePageWidth <= 0) return;
        boolean pageChanged = false;
        // 滑动超过一页说明已翻页
        if (Math.abs(mCurrentItemOffset - mCurrentItemPos * mOnePageWidth) >= mOnePageWidth) {
            pageChanged = true;
        }
        if (pageChanged) {
            int tempPos = mCurrentItemPos;

            mCurrentItemPos = mCurrentItemOffset / mOnePageWidth;
        }
    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private void onScrolledChangedCallback() {
        int offset = mCurrentItemOffset - mCurrentItemPos * mOnePageWidth;
        float percent = (float) Math.max(Math.abs(offset) * 1.0 / mOnePageWidth, 0.0001);

        View leftView = null;
        View currentView;
        View rightView = null;
        if (mCurrentItemPos > 0) {
            leftView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos - 1);
        }
        currentView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos);
        if (mCurrentItemPos < mRecyclerView.getAdapter().getItemCount() - 1) {
            rightView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos + 1);
        }

        if (leftView != null) {
            // y = (1 - mScale)x + mScale
            leftView.setScaleY((1 - mScale) * percent + mScale);
        }
        if (currentView != null) {
            // y = (mScale - 1)x + 1
            currentView.setScaleY((mScale - 1) * percent + 1);
        }
        if (rightView != null) {
            // y = (1 - mScale)x + mScale
            rightView.setScaleY((1 - mScale) * percent + mScale);
        }
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public void setPagePadding(int pagePadding) {
        mPagePadding = pagePadding;
    }

    public void setShowLeftCardWidth(int showLeftCardWidth) {
        mShowLeftCardWidth = showLeftCardWidth;
    }


    public StillCallBack getStillCallBack() {
        return stillCallBack;
    }

    public void setStillCallBack(StillCallBack stillCallBack) {
        this.stillCallBack = stillCallBack;
    }

    StillCallBack stillCallBack;

    public interface StillCallBack {
        void still();
    }
}
