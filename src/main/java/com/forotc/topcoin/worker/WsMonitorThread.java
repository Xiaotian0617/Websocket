package com.forotc.topcoin.worker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class WsMonitorThread implements Runnable {

    private ThreadPoolExecutor executor;
    private int delay;
    private int tps;
    private volatile boolean run = true;

    private static final int MILLISECONDS_OF_MINUTES = 1000 * 60;

    /**
     * 构造方法
     *
     * @param executor 线程执行器
     * @param tps      每秒中采样的次数 time per second
     */
    public WsMonitorThread(ThreadPoolExecutor executor, int tps) {
        if (tps < 1) {
            tps = 1;
        }
        this.executor = executor;
        this.tps = tps;
        this.delay = 3000;
    }


    /**
     * 构造方法
     *
     * @param executor 线程执行器
     * @param delay    暂缓启动时长，单位 ms
     * @param tpm      每秒中采样的次数 time per min
     */
    public WsMonitorThread(ThreadPoolExecutor executor, int delay, int tpm) {
        int tps = tpm;
        tps = tps < 1 ? 1 : tps;
        tps = tps > 120 ? 120 : tps;
        this.executor = executor;
        this.tps = tps;
        this.delay = delay;
    }

    public void shutdown() {
        this.run = false;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        while (run) {
            log.info("[monitor] [{}/{}] Active:{}, Completed: {}, Task: {}, isShutdown: {}, isTerminated: {}",
                            this.executor.getPoolSize(),
                            this.executor.getCorePoolSize(),
                            this.executor.getActiveCount(),
                            this.executor.getCompletedTaskCount(),
                            this.executor.getTaskCount(),
                            this.executor.isShutdown(),
                            this.executor.isTerminated());

            try {
                Thread.sleep(MILLISECONDS_OF_MINUTES / tps);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

    }
}
