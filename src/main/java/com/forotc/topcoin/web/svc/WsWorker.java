//package com.forotc.topcoin.web.svc;
//
//import lombok.extern.slf4j.Slf4j;
//
//import javax.websocket.Session;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//@Slf4j
//public class WsWorker implements Runnable {
//
//    private String key, data;
//
//    private Map<String, Session> uis2sessions;
//    private Map<String, Boolean> uidKeyFlags;
//
//    public WsWorker(Map<String, Session> uis2sessions, Map<String, Boolean> uidKeyFlags, String key, String data) {
//        this.key = key;
//        this.data = data;
//        this.uis2sessions = uis2sessions;
//        this.uidKeyFlags = uidKeyFlags;
//    }
//
//    @Override
//    public void run() {
//        fireWsMsg();
//    }
//
//    private void fireWsMsg() {
//
//        Set<Map.Entry<String, Session>> entrySet = uis2sessions.entrySet();
//
//        for (Entry<String, Session> entry : entrySet) {
//            String uid = entry.getKey();
//            String uidKey = uid + "-" + key;
//            Boolean uidKeyFlag = uidKeyFlags.get(uidKey);
//
//            log.debug("{}: fireMsg: uid:{}, key:{}, uid-key:{}, uidKeyFlag:{}", Thread.currentThread().getName(), uid, key, uidKey, uidKeyFlag);
//            if (uidKeyFlag != null && uidKeyFlag) {
//                Session ss = uis2sessions.get(uid);
//                try {
//                    if (ss != null) {
//                        log.debug("{}: fireMsg: sent to->{}:{}:{}", Thread.currentThread().getName(), uid, key, data);
//                        synchronized (WsSvc.SYNC_SEND_MSG) {
//                            ss.getBasicRemote().sendText(data);
//                        }
//                    } else {
//                        log.error(Thread.currentThread().getName() + ":fireMsg: This uid(" + uid + ") not having valid session!");
//                    }
//                } catch (IOException e) {
//                    log.error(Thread.currentThread().getName() + ":fireMsg: error in push to websoket:" + uid + ":" + ss + ":" + data, e);
//                }
//            }
//        }
//    }
//
//}
