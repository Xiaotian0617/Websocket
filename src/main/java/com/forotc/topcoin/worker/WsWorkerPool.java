//package com.forotc.topcoin.util.tp;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.*;
//
//@Configuration
//@Slf4j
//public class WsWorkerPool {
//
//
//    @Value("${tp.corePooSize}")
//    private int corePoolSize;
//
//    @Value("${tp.maximumPoolSize}")
//    private int maximumPoolSize;
//
//    @Value("${tp.keepAliveTime}")
//    private int keepAliveTime;
//
//    @Value("${tp.queueCapcity}")
//    private int queueCapcity;
//
//    @Value("${tp.single}")
//    private boolean single;
//
//	@Bean
//	public ThreadPoolExecutor getThreadPoolExecutor() {
//
//		ThreadPoolExecutor executorPool = null;
//
//		log.info("getThreadPoolExecutor:" + single  + corePoolSize );
//		if(single) {log.info("getThreadPoolExecutoryyyy:" + single  + corePoolSize );}
//
//		/* for single threaded , no need to triger below */
//		if (!single) {
//
//			WsRejectedExecutionHandlerImpl rejectionHandler = new WsRejectedExecutionHandlerImpl();
//			ThreadFactory threadFactory = Executors.defaultThreadFactory(); // Get the ThreadFactory implementation to
//																			// use
//			executorPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueCapcity),
//					threadFactory, rejectionHandler);
//
//			WsMonitorThread monitor = new WsMonitorThread(executorPool, 3);
//			Thread monitorThread = new Thread(monitor);
//			monitorThread.start();
//		}
//		return executorPool;
//	}
//
//}
