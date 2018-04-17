package com.tablet.moran.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * recycler view 的 base adapter
 * Created by stone on 2016/3/27.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {

    public final static int HEADERTYPE = 0;
    public final static int NORMALTYPE = 1;
    public final static int FOOTERTYPE = 2;
    public final static int TITLETYPE = 3;

    public View mHeaderView;
    public View mFooterView;
    public View mTitleView;
    public View mView;

    public List<T> mData = new ArrayList<>();
    public Context mContext;
    public OnItemClickListener itemClickListener;
    public LayoutInflater mInflater;

    public BaseRecyclerAdapter(Context context, List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        if (datas != null) {
            mData = datas;
        }
    }


    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == HEADERTYPE)
            return new BaseRecyclerHolder(mHeaderView);
        else if (mFooterView != null && viewType == FOOTERTYPE)
            return new BaseRecyclerHolder(mFooterView);
        return createViewHoldeHolder(parent, viewType);
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;

    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, int position) {
        //header的view
        if (getItemViewType(position) == HEADERTYPE && mHeaderView != null) return;
        //footer的view
        if (getItemViewType(position) == FOOTERTYPE && mFooterView != null) return;
        showViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) return NORMALTYPE;
        else if (mHeaderView != null && mFooterView != null)
            if (position == 0) return HEADERTYPE;
            else if (position == getItemCount() - 1)
                return FOOTERTYPE;
            else
                return NORMALTYPE;
        else if (mHeaderView != null)
            if (position == 0) return HEADERTYPE;
            else return NORMALTYPE;
        else {
            if (position == getItemCount() - 1) return FOOTERTYPE;
            else return NORMALTYPE;
        }

    }

    @Override
    public int getItemCount() {

        if (mData == null)
            throw new NullPointerException("未给adapter添加数据");
        else if (mFooterView == null)
            return mHeaderView == null ? mData.size() : mData.size() + 1;
        else {
            return mHeaderView == null ? mData.size() + 1 : mData.size() + 2;
        }

    }

    //设置header
    public View getmHeaderView() {
        return mHeaderView;
    }

    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
    }

    //设置footer
    public View getmFooterView() {
        return mFooterView;
    }

    public void setmFooterView(View mFooterView) {
        this.mFooterView = mFooterView;
    }


    public List<T> getDatas() {
        return mData;
    }

    public void addItemLast(List<T> datas) {
        mData.addAll(datas);
    }

    public void setmData(List<T> datas) {
        mData = datas;
    }

    public void remove(int position) {
        mData.remove(position);
    }

    public void removeAll() {
        mData.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 点击事件接口
    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T model);

        void onItemLongClick(View view, int position, T model);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public abstract BaseRecyclerHolder createViewHoldeHolder(ViewGroup parent, int viewType);
    public abstract void showViewHolder(BaseRecyclerHolder holder, int position);

}
