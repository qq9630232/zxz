package com.tablet.moran.event;

/**
 * Created by ASUS on 2016/8/1.
 */
public class OrientEvent {

    public static final int POR = 0;
    public static final int LAND = 1;

    public OrientEvent(int flag) {
        this.orient = flag;
    }
    private int orient;//0:portrait  1:landcape

    public int getOrient() {
        return orient;
    }

    public void setOrient(int orient) {
        this.orient = orient;
    }
}
