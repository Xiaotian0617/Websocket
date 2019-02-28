package com.forotc.topcoin.web.ws;


import com.forotc.topcoin.service.WorkerForSubImpl;
import com.forotc.topcoin.service.WsService;
import lombok.extern.slf4j.Slf4j;
import com.forotc.topcoin.conf.WsContextProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Component
@ServerEndpoint(value = "/ws/{uid}", configurator = WsContextProvider.class)
public class WsServer {

    private static final AtomicLong onlineCount = new AtomicLong(0);
    private static final String DEFAULT_UNIQUE_KEYS = "Bitfinex_BTC_USD,Bitfinex_ETH_USD,Bitfinex_LTC_USD";
    public static final int SUBSCRIBE_USER_TOURISTS = 0;
    public static final int SUBSCRIBE_USER_CUSTOMER = 1;

    private Object obj = new Object();


    private String uid;
    private Object lock = new Object();
    @Resource
    WsService wsSvc;

    @Resource
    ThreadPoolExecutor subExecutor;

    //static WsService wsSvc;
   /* @Autowired
    public void setWsSvc(WsService wsSvc){
        WsServer.wsSvc = wsSvc;
    }*/

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        synchronized (obj){
            this.uid = uid;
            //type 0 ： 游客 。 1： 用户
            int type = "".equals(uid)?SUBSCRIBE_USER_TOURISTS:SUBSCRIBE_USER_CUSTOMER;
            if ("".equals(this.uid)) {
                this.uid = UUID.randomUUID().toString().replace("-","");
            }
            //用户订阅相关操作，异步处理
            subExecutor.execute(new WorkerForSubImpl(DEFAULT_UNIQUE_KEYS, this.uid,session,type,wsSvc));
        }

        log.info("New session opened, current connections {}",onlineCount.incrementAndGet());

    }

//    @OnOpen
//    public void onOpen(Session session, @PathParam("uid") String uid) {
//        this.uid = uid;
//
//        if (wsSvc == null) {
//            wsSvc = WsContextProvider.getBean(WsService.class);
//        }
//
//        if ("".equals(uid)) {
//            uid = UUID.randomUUID().toString().replace("-","");
//            wsSvc.subscript(uid,DEFAULT_UNIQUE_KEYS);
//        }
//        wsSvc.registerSession(uid, session);
//        log.info("New session opened, uid is {}, current connections {}",uid,onlineCount.incrementAndGet());
//    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message != null && message.equalsIgnoreCase("ping")) {
            try {
                log.debug(" onMessage Pong: " + ByteBuffer.wrap("health-check".getBytes()));
                //session.getBasicRemote().sendPong(ByteBuffer.wrap("health-check".getBytes()));
                session.getBasicRemote().sendText("pong");
            } catch (IOException e) {
                log.error("onMessage： ERROR !", e);
            }
            return;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("onClose: One closed, current connections {}", onlineCount.decrementAndGet());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("onError: " + throwable.getMessage());
    }
}
