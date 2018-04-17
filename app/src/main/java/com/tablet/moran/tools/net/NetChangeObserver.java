package com.tablet.moran.tools.net;
/**
 * 
 * @author Tristan Han
 *
 * 2014-11-23
 * 检测网络改变的观察者
 */
public interface NetChangeObserver {
    /**
     * 网络连接连接时调用
     */
    public void onConnect(NetWorkUtil.NetType type);

    /**
     * 当前没有网络连接
     */
    public void onDisConnect() ;
}
