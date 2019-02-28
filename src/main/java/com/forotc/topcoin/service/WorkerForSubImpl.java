package com.forotc.topcoin.service;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/3/11 16:14
 */
@Slf4j
public class WorkerForSubImpl implements Runnable {

    private static String defaultUniqueKeys;
    private String uid;
    private Session sessions;
    private int type;
    private WsService wsSvc;

    private WorkerForSubImpl() {
    }

    public WorkerForSubImpl(final String subKeys,final String subUid, final Session subSession,final int userType, WsService subSsServer) {
        this.sessions = subSession;
        this.uid = subUid;
        this.defaultUniqueKeys = subKeys;
        this.type = userType;
        this.wsSvc = subSsServer;
    }

    @Override
    public void run() {
        //如果用户是游客，给予默认订阅信息
        if (this.type == 0) {
            wsSvc.subscript(this.uid, this.defaultUniqueKeys);
        }
        //注册session信息
        wsSvc.registerSession(uid, this.sessions);
    }

}
