package com.hframework.common.frame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 */
public abstract class ThreadTemplate implements Runnable {

    private CountDownLatch threadsSignal;

    public ThreadTemplate(CountDownLatch threadsSignal) {
        this.threadsSignal = threadsSignal;
    }

    public void run() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "开始时间：" + formatter.format(new Date(startTime)));
        System.out.println(Thread.currentThread().getName() + "开始...");
        // 业务处理
        business();
        // Do somethings
        threadsSignal.countDown();//线程结束时计数器减1
        long endTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "结束时间：" + formatter.format(new Date(endTime)));
        System.out.println(Thread.currentThread().getName() + "结束. 还有" + threadsSignal.getCount() + " 个线程");
    }

    /**
     * 业务处理
     *
     * @param objects
     */
    public abstract void business(Object... objects);


}
