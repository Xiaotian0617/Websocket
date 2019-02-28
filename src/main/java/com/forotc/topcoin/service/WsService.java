package com.forotc.topcoin.service;


import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * WebSocket 所需的服务接口
 */
public interface WsService {

    /**
     * 订阅
     * @param keys
     * @return
     */
    boolean subscript(String uid, String keys);

    /**
     * 取消订阅
     * @return
     */
    boolean unSubscript(String uid);


    /**
     * 重新订阅消息
     * 取消用户的所有订阅消息，然后重新订阅消息
     * 其功能实现等同于:
     * unSubscript(String uid); subscript(String uid, String keys)
     * @param uid   用户id
     * @param keys  订阅的keys
     * @return
     */
    boolean reSubscript(String uid, String keys);

    /**
     * 通过uid拿到用户对应的session，此部分的实现只能在同一个WebSocket Container内部实现
     * 不能做分布式的实现，所以要求在配置Nginx时使用ip hash 方式或者是sticky
     * @param uid   用户标识id
     * @return      此用户对应的session对象
     */
    Session getSession(String uid);


    /**
     *  移除指定用户的session信息
     * @param uid   指定的用户id
     * @return
     */
    boolean removeSession(String uid);


    /**
     * 用户注册session
     * @param uid       用户id
     * @param session   注册的 web socket的session
     * @return
     */
    boolean registerSession(String uid, Session session);


    /**
     * 处理
     * @param list
     * @return
     */
    void process(String tx,List<Map<String,Object>> list);

    /**
     * 获取sessionMap
     *
     * @return 所有注册的sessionMap
     */
    ConcurrentMap<String, Session> getSessionMap();

}
