package com.tablet.moran.clazz;

/**
 * 调用native方法
 * Created by zhaohZhaoHee on 15/10/19.
 */

public class BulrManager {
    static
    {
        System.loadLibrary("blurjni");
    }
    public native static int[] StackBlur(int pix[], int w, int h, int radius) ;

}
