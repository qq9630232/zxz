package com.tablet.moran.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.tablet.moran.adapter.BaseRecyclerAdapter;
import com.tablet.moran.tools.SLogger;


public class RefreshLayout extends SwipeRefreshLayout {


    private final int mTouchSlop;
    private RecyclerView mRecyclerview;
    private OnLoadListener mOnLoadListener;
    private LinearLayoutManager llayoutManager;
    private GridLayoutManager glayoutManager;
    private BaseRecyclerAdapter adapter;
    //该recyclerview是否含有footer
    private boolean hasFooter = false;

    private float firstTouchY;
    private float lastTouchY;

    private boolean isLoading = false;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    //set the child view of RefreshLayout,ListView
    public void setChildView(RecyclerView mListView) {
        this.mRecyclerview = mListView;
        RecyclerView.LayoutManager llm = mRecyclerview.getLayoutManager();
        adapter = (BaseRecyclerAdapter) mRecyclerview.getAdapter();
        if (adapter.getmFooterView() != null) {
            hasFooter = true;
        }
        if (llm instanceof LinearLayoutManager)
            this.llayoutManager = (LinearLayoutManager) llm;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstTouchY = event.getRawY();
                break;

            case MotionEvent.ACTION_UP:

                lastTouchY = event.getRawY();
                if (canLoadMore()) {
                    loadData();
                }
                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean canLoadMore() {
        return isBottom() && !isLoading && isPullingUp();
    }

    private boolean isBottom() {
            if (adapter.getItemCount() > 0) {
                if (llayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    return true;
                }
            }
            return false;
//        }

    }

    private boolean isPullingUp() {
        return (firstTouchY - lastTouchY) >= mTouchSlop;
    }

    private void loadData() {
        if (mOnLoadListener != null) {
            if (isLoading)
                return;
            setLoading(true);
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        if (mRecyclerview == null) return;
        isLoading = loading;
        if (loading) {
            if (isRefreshing()) {
                setRefreshing(false);
            }
//            llayoutManager.scrollToPosition(adapter.getItemCount()-1);
            /**
             * adapter.getItemCount()-1.....
             */
            SLogger.d("<<", "--->>>>down55555555");

            llayoutManager.scrollToPositionWithOffset(adapter.getItemCount()-1, 0);
            mOnLoadListener.onLoad();
        } else {
            firstTouchY = 0;
            lastTouchY = 0;
        }
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    public interface OnLoadListener {
        public void onLoad();
    }
}