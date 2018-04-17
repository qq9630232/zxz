package com.tablet.moran.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tablet.moran.R;
import com.tablet.moran.model.TitleInfo;
import com.tablet.moran.tools.DensityUtils;
import com.tablet.moran.tools.ImageLoader;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ScreenUtils;
import com.tablet.moran.view.gallery.CardAdapterHelper;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tablet.moran.activity.BaseActivity.CHINESE;
import static com.tablet.moran.activity.BaseActivity.LANGUAGE;

/**
 * Created by ASUS on 2017/10/24.
 * <p>
 * 主页的镜像 recycler显示
 */

public class MainRecyclerAdaper extends BaseRecyclerAdapter<TitleInfo> {

    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private int mPagePadding = 30;
    private int mCardWidthL = 680;
    private int mCardWidthP = 480;
    private float mScale = 1.4f;

    public MainRecyclerAdaper(Context context, List<TitleInfo> datas) {
        super(context, datas);
    }

    @Override
    public BaseRecyclerHolder createViewHoldeHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_item, parent, false);
        return new MainHolder(itemView);
    }

    @Override
    public void showViewHolder(BaseRecyclerHolder holder, int position) {
        final TitleInfo painting = mData.get(position);
        final MainHolder mainHolder = (MainHolder) holder;
        if (position == 0) {

            //TODO 下面的代码到底要不要乘2
//            int leftMargin = (ScreenUtils.getScreenWidth(mContext) - (int) (DensityUtils.dip2px(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? mCardWidthP : mCardWidthL) * mScale)) / 2;
            int leftMargin = (ScreenUtils.getScreenWidth(mContext) - (int) (DensityUtils.dip2px(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? mCardWidthP : mCardWidthL))) / 2;
            setViewMargin(mainHolder.itemView, leftMargin, 0, 0, 0);
        } else if (position == mData.size() - 1) {
            int rightMargin = (ScreenUtils.getScreenWidth(mContext) - (int) (DensityUtils.dip2px(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? mCardWidthP : mCardWidthL))) / 2;
            setViewMargin(mainHolder.itemView, 0, 0, rightMargin, 0);
        }

        SLogger.d("<<", "==>>>>" + painting.getThumbPath());
        if (painting.getPaint_id() == -1){
            mainHolder.paintName.setText(mContext.getResources().getString(R.string.default_paint));
        } else if(painting.isNews()){
            mainHolder.paintName.setText(mContext.getResources().getString(R.string.news_paint));
        }else {
            mainHolder.paintName.setText(painting.getPaint_title());
        }
        try {
            //TODO 这里可以试一下setBackground， 如果宽高比不合适的话
            ImageLoader.displayImg(mContext, new File(painting.getThumbPath()), mainHolder.imageRecycler);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void setViewMargin(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (lp.leftMargin != left || lp.topMargin != top || lp.rightMargin != right || lp.bottomMargin != bottom) {
            lp.setMargins(left, top, right, bottom);
            view.setLayoutParams(lp);
        }
    }

    /**
     * is chinese
     * @return
     */
    public boolean isChinese() {
        if(PreferencesUtils.getInt(mContext, LANGUAGE) == CHINESE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 没有正在进行
     */
    public class MainHolder extends BaseRecyclerHolder {

        @BindView(R.id.image_recycler)
        ImageView imageRecycler;
        @BindView(R.id.paint_name)
        TextView paintName;

        public MainHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
