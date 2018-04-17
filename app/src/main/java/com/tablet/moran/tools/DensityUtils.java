package com.tablet.moran.tools;

import android.content.Context;

/**
 *
 * 显示单位转换类：px2dp，dp2px etc.
 * Created by tristan on 15/10/27.
 */
public class DensityUtils {  
	 
	private static Context mContext;
	public static void initDesityUtils(Context context){
		mContext = context;
	}
	private DensityUtils(){
		throw new UnsupportedOperationException("can not instantiated");
	}
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(float dpValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(float pxValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    /** 
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素) 
     */  
    public static int sp2px(float spValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp 
     */  
    public static int px2sp(float spValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue / scale + 0.5f);  
    }  
    
}  