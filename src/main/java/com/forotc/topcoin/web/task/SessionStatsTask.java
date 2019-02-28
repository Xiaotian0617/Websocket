package com.forotc.topcoin.web.task;

import com.forotc.topcoin.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/3/11 17:56
 */
@Slf4j
@Component
public class SessionStatsTask {

    @Resource
    WsService wsSvc;

    @Scheduled(initialDelay=10 * 1000, fixedDelay= 30 * 60 * 1000)
    public void reportCurrentTime() {
        final AtomicLong clearSum = new AtomicLong(0);
        try {
            wsSvc.getSessionMap().forEach((uid, session) -> {
                if (!session.isOpen()) {
                    wsSvc.unSubscript(uid);
                    wsSvc.removeSession(uid);
                    clearSum.incrementAndGet();
                }
             });
            log.info(" Clear not enabled session ; sessionMap size : {} ,clear failure sum {}",wsSvc.getSessionMap().size(),clearSum);
            clearSum.intValue();
        } catch (Exception e) {
            log.error(" SessionStatsTask ", e);
        }

    }

}
