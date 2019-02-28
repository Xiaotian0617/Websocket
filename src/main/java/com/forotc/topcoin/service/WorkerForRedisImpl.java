package com.forotc.topcoin.service;

import javax.websocket.Session;
import java.util.Map;

/**
 * @author Asin Liu
 * @since 1.0
 * @version 1.0
 */
public class WorkerForRedisImpl implements Runnable {
    private Map<String,Object> data;
    private Map<String,Session> sessions ;
    private Sender sender;
    public WorkerForRedisImpl(Sender sender,Map<String,Object> data, final Map<String ,Session> sessionMap) {
        this.sender = sender;
        this.data = data;
        this.sessions = sessionMap;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        sender.sendMessage(data,this.sessions);
    }
}
