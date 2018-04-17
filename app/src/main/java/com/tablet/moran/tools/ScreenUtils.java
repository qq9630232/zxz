package com.tablet.moran.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenUtils {

	private ScreenUtils(){
		throw new UnsupportedOperationException("can not instantiated");
	}
	/**
	 * 获得屏幕宽度
	 * @param context
	 * @return 宽度px值
	 */
	public static int getScreenWidth(Context context){
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
	/**
	 * 获得屏幕高度
	 * @param context
	 * @return 高度px值
	 */
	public static int getScrrenHeight(Context context){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView) { 
	    if(listView == null) return;

	    ListAdapter listAdapter = listView.getAdapter(); 
	    if (listAdapter == null) { 
	        // pre-condition 
	        return; 
	    } 

	    int totalHeight = 0; 
	    for (int i = 0; i < listAdapter.getCount(); i++) { 
	        View listItem = listAdapter.getView(i, null, listView); 
	        listItem.measure(0, 0); 
	        totalHeight += listItem.getMeasuredHeight(); 
	    } 

	    ViewGroup.LayoutParams params = listView.getLayoutParams(); 
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		Log.d("-->>","total height = "+params.height);
	    listView.setLayoutParams(params); 
	}

	/**
	 * 判断手机是否含有虚拟键
	 * @param context
	 * @return
	 */
	public static boolean isHasPermanentMenuKey(Context context){
		return ViewConfiguration.get(context).hasPermanentMenuKey();
	}

	/**
	 * 获取手机底部虚拟键高度
	 * @param nContext
	 * @return height
	 */
	public static int getNavigationBarHeight(Context nContext) {
		Resources resources = nContext.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
		int height = resources.getDimensionPixelSize(resourceId);
		Log.v("<<", "Navi height:" + height);
		return height;
	}


	/**
	 * scroll Vertical
	 * @param y 垂直滑动的距离
	 */
	public static void scrollVertical(final ListView listView, Activity activity, final int y){
		if(listView == null)
			return;
		activity.runOnUiThread(new Runnable() { //执行自动化测试的时候模拟滑动需要进入UI线程操作
			@Override
			public void run() {
				listView.smoothScrollBy(y,300);
//				invokeMethod(listView, "trackMotionScroll", new Object[]{-y, -y}, new Class[]{int.class, int.class});
			}
		});
	}

	/**
	 * 遍历当前类以及父类去查找方法，例子，写的比较简单
	 * @param object
	 * @param methodName
	 * @param params
	 * @param paramTypes
	 * @return
	 */
	public static Object invokeMethod(Object object, String methodName, Object[] params, Class[] paramTypes){
		Object returnObj = null;
		if (object == null) {
			return null;
		}
		Class cls = object.getClass();
		Method method = null;
		for (; cls != Object.class; cls = cls.getSuperclass()) { //因为取的是父类的默认修饰符的方法，所以需要循环找到该方法
			try {
				method = cls.getDeclaredMethod(methodName, paramTypes);
				break;
			} catch (NoSuchMethodException e) {
//					e.printStackTrace();
			} catch (SecurityException e) {
//					e.printStackTrace();
			}
		}
		if(method != null){
			method.setAccessible(true);
			try {
				returnObj = method.invoke(object, params);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return returnObj;
	}

	public static void showStatusBar(View view){
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
	}

	public static void hideStatusBar(View view){
		view.setSystemUiVisibility(View.INVISIBLE);
	}
}
