package com.tablet.moran.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 通用ViewPager pager adapter
 * 用于banner 等
 * @author Tristan Han
 *
 * 2015-3-19 上午10:34:45
 */
public abstract class MeBasePagerAdapter<T> extends PagerAdapter {
	
	protected List<T> dataList;
	protected Context context;
	
	public MeBasePagerAdapter(List<T> dataList, Context context){
		this.dataList = dataList;
		this.context = context;
	}
	@Override
	public int getCount() {
		if (dataList==null) {
			return 0;
		}
		return dataList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		T t = dataList.get(position);
		View view = fetchItemView(t);
		container.addView(view);
		return view;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}
	
	public abstract View fetchItemView(T t);
	
}
