package com.tablet.moran.tools;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tristan on 15/11/10.
 */

public class ThreadPoolManager {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private TimeUnit unit = TimeUnit.HOURS;
    private ThreadPoolExecutor threadPoolExecutor;

    static ThreadPoolManager manager = new ThreadPoolManager();

    private ThreadPoolManager() {

        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        maximumPoolSize = corePoolSize;
        keepAliveTime = 1;

        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                unit,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolManager getinstance() {
        return manager;
    }

    public void execute(Runnable runnable) {
        if (runnable != null) {
            threadPoolExecutor.execute(runnable);
        }
    }

    public void remove(Runnable runnable) {
        if (runnable != null) {
            threadPoolExecutor.remove(runnable);
        }
    }

}
