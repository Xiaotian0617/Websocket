//package com.forotc.topcoin.web.svc;
//
//import com.forotc.topcoin.util.TimeUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.Session;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Component
//@Slf4j
//public class WsSvc {
//
//    static Map<String, Session> uis2sessions = new ConcurrentHashMap<>();
//
//    static Map<String, Boolean> uidKeyFlags = new ConcurrentHashMap<>();
//
//    public static final Object SYNC_SEND_MSG = new Object();
//
//    @Value("${tp.single}")
//    private boolean single;
//
//    @Value("${ws.debug}")
//    private boolean wsDebug;
//
//    @Value("${ws.fireall}")
//    private boolean fireall = false;
//
//
//
//    @Autowired
//    ThreadPoolExecutor threadPoolExecutor;
//
//    public String getUidSessions() {
//        return uis2sessions.toString();
//    }
//
//    public String getUidKeyFlags() {
//        return uidKeyFlags.toString();
//    }
//
//
//    /*
//     * if the key found in the uid associated hashset, then data push required
//     */
//    public boolean updateRegisteredKeys(String uid, String keys, boolean flag) {
//        if (!uis2sessions.containsKey(uid)) {
//            log.warn("用户:{},未连接", uid);
//            return false;
//        }
//        log.debug("updateRegisteredKeys: keys:{}", keys);
//
//        if (keys == null || keys.equals("")) {
//            switchUidKeyFlags(uid, false);
//            return true;
//        }
//
//        String[] ary = keys.split(",");
//        for (int i = 0; i < ary.length; i++) {
//            uidKeyFlags.put(uid + '-' + ary[i].trim(), flag);
//        }
//        log.debug("updateRegisteredKeys: {} {}", uidKeyFlags.size(), uidKeyFlags.toString());
//        return true;
//    }
//
//    /**
//     * Save this user's web-socket session into hashmap so other method' can get
//     * session through uid
//     *
//     * @param uid
//     * @param session
//     */
//    public boolean addSession(String uid, Session session) {
//        if (uis2sessions.containsKey(uid)) {
//            log.warn("addSession: " + uid + " already opened a web-scoket!");
//            Session oldSession = uis2sessions.get(uid);
//            try {
//                oldSession.close();
//                uis2sessions.remove(uid);
//                uis2sessions.put(uid, session);
//                session.getBasicRemote().sendText("Uid(" + uid + ") already opened one, make old closed and use newly opened!");
//                switchUidKeyFlags(uid, true);
//            } catch (IOException e) {
//                log.error("onOpen: session.getBasicRemote().sendText(msg) failed for UID:" + uid);
//            }
//
//            return false;
//        }
//
//        uis2sessions.put(uid, session);
//        log.debug("WsSvc.addSession: uis2sessions: " + uis2sessions.size()); //+ ": " + uis2sessions.toString());
//        return true;
//    }
//
//    public void clearSession(String uid, Session session) {
//        Session u2ss = uis2sessions.get(uid);
//        if (u2ss != null && u2ss.equals(session)) {
//            log.info("clearSession:  session form hashmap get destroied!");
//        } else {
//            log.warn("clearSession: this uid:" + uid + "has problem, which caused by duplicate uid problem!");
//        }
//
//        uis2sessions.remove(uid);
//        switchUidKeyFlags(uid, false);
//
//        try {
//            session.close();
//        } catch (IOException e) {
//            log.error("Error occured in session.close()!" + e.getMessage(), e);
//        }
//        log.info("clearSession: uis2session:" + uis2sessions.size()); //+ ": " + uis2sessions.toString());
//    }
//
//    private void switchUidKeyFlags(String uid, boolean flag) {
//        log.debug("turnfalseUidKeyFlags: before:{}", uidKeyFlags.toString());
//        for (String key : uidKeyFlags.keySet()) {
//            String prefix = uid + "-";
//            if (key.startsWith(prefix)) {
//                log.info("turnfalseUidKeyFlags: startsWith:" + key + " prefix:" + prefix);
//                uidKeyFlags.put(key, flag);
//            }
//        }
//        log.debug("turnfalseUidKeyFlags: after: {},{}", uidKeyFlags.size(), uidKeyFlags.toString());//toString());
//    }
//
//    // static private Set<Map.Entry<String, Session>> entrySet =
//    // uis2sessions.entrySet();
//    /*
//     * for all the uid in uidSessions, check if this user has key registered to push
//     * msg to client key, from agent representing the trade-center's specific data
//     */
//    public boolean flushWs(String key, String data) {
//        long then = System.nanoTime();
//
//        log.debug("flushWs: {} :{}", key, data);
//
//        if (single) {
//            try {
//                singleTheadedFireMsg(key, data);
//            } catch (IOException e) {
//                log.error("flusWS: error occured when pushing data to client side" + e.getMessage(), e);
//                return false;
//            }
//        } else {
//            log.debug("flushWs: theadpooled!{}", threadPoolExecutor);
//            threadPoolExecutor.execute(new WsWorker(uis2sessions, uidKeyFlags, key, data));
//        }
//        long now = System.nanoTime();
//        TimeUtil.printElapsedTime("flusWs", now - then);
//        return true;
//    }
//
//    private void singleTheadedFireMsg(String key, String data) throws IOException {
//
//        Set<Map.Entry<String, Session>> entrySet = uis2sessions.entrySet();
//        for (Entry<String, Session> entry : entrySet) {
//            String uid = entry.getKey();
//            String uidKey = uid + "-" + key;
//            Boolean uidKeyFlag = uidKeyFlags.get(uidKey);
//
//            if (!fireall) {
//                log.debug("uidkey:{},uidkeyflag:{}", uidKey, uidKeyFlag);
//                if (uidKeyFlag != null && uidKeyFlag) {
//                    fireMsg(uid, data, key);
//                }
//            } else {
//                fireMsg(uid, data, key);
//            }
//        }
//    }
//
//    private void fireMsg(String uid, String data, String key) throws IOException {
//        long then = System.nanoTime();
//
//        Session ss = uis2sessions.get(uid);
//        if (ss != null) {
//            log.debug("singleTheadedFireMsg: sent to->{}:{}:{}", uid, key, data);
//            synchronized (SYNC_SEND_MSG) {
//                ss.getBasicRemote().sendText(data);
//            }
//        } else {
//            log.error("singleTheadedFireMsg: This uid(" + uid + ") not having valid session!");
//        }
//
//        long now = System.nanoTime();
//        TimeUtil.printElapsedTime("fireMsg", now - then);
//    }
//
//    /*
//     * Through uid associated websocket session push msg to client side
//     */
//
////	private void sendMsg(String uid, String msg) {
////		Session session = uis2sessions.get(uid);
////		if (session != null)
////			try {
////				session.getBasicRemote().sendText(msg);
////			} catch (IOException e) {
////				log.error("sendMsg: session.getBasicRemote().sendText(msg) failed for UID:" + uid,e);
////			}
////		else
////			log.error("sendMsg: There is no session for UID:" + uid);
////	}
//
//    public void setFireall(boolean flag) {
//        this.fireall = flag;
//    }
//
//    public void setWsdebug(boolean flag) {
//        this.wsDebug = flag;
//    }
//}
