package com.forotc.topcoin.service.impl;

import com.forotc.topcoin.service.WsService;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.websocket.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Slf4j
public class DefaultWsServiceImpl implements WsService {

    protected static ConcurrentHashMap<String, CopyOnWriteArraySet<String>> onlyKey2User = new ConcurrentHashMap<>(4096);

    protected static ConcurrentMap<String, Session> sessionMap = new ConcurrentHashMap<>(1024);
    protected static ConcurrentMap<String, String> userLastSubscript = new ConcurrentHashMap<>(1024);

    /**
     * 订阅
     *
     * @param uid
     * @param keys
     * @return
     */
    @Override
    public boolean subscript(String uid, String keys) {
        throw new NotImplementedException();
        //return false;
    }

    /**
     * 取消订阅
     *
     * @param uid
     * @return
     */
    @Override
    public boolean unSubscript(String uid) {
        throw new NotImplementedException();
        //return false;
    }

    /**
     * 重新订阅消息
     * 取消用户的所有订阅消息，然后重新订阅消息
     * 其功能实现等同于:
     * unSubscript(String uid); subscript(String uid, String keys)
     *
     * @param uid  用户id
     * @param keys 订阅的keys
     * @return
     */
    @Override
    public boolean reSubscript(String uid, String keys) {
        List<String> ks = validKeys(keys);


        return false;
    }

    /**
     * 过滤掉其中的空白字符
     * @param onlyKeys  onlyKey
     * @return          符合条件的列表
     */
    private List<String> validKeys(String onlyKeys) {
        if (!isBlank(onlyKeys)) {
            try {
                String[] oks = onlyKeys.split(",");
                return Arrays.stream(oks).filter(o -> !isBlank(o)).collect(Collectors.toList());
            } catch (Exception e) {
                log.error(">>> 解析only key时出错，传入的only keys:{}", onlyKeys);
            }
        }
        return null;
    }

    private boolean isBlank(String src) {
        return null == src || "".equals(src.trim());
    }

    /**
     * 通过uid拿到用户对应的session，此部分的实现只能在同一个WebSocket Container内部实现
     * 不能做分布式的实现，所以要求在配置Nginx时使用ip hash 方式或者是sticky
     *
     * @param uid 用户标识id
     * @return 此用户对应的session对象
     */
    @Override
    public Session getSession(String uid) {
        return sessionMap.get(uid);
    }

    /**
     * 移除指定用户的session信息
     *
     * @param uid 指定的用户id
     * @return
     */
    @Override
    public boolean removeSession(String uid) {
        try {
            sessionMap.remove(uid);
        } catch (Exception e) {
            log.error(">>>移除用户session时失败，待移除的用户id: {}", uid);
        }
        return true;
    }

    /**
     * 用户注册session
     *
     * @param uid     用户id
     * @param session 注册的 web socket的session
     * @return
     */
    @Override
    public boolean registerSession(String uid, Session session) {
        try {
            sessionMap.put(uid, session);
        } catch (Exception e) {
            log.error(">>> 注册用户session 时失败，注册用户:{}, session:{}", uid, session);
            return false;
        }
        return true;
    }

    /**
     * 处理
     *
     * @param list
     * @return
     */
    @Override
    public void process(String tx,List<Map<String,Object>> list) {

    }

    @Override
    public ConcurrentMap<String, Session> getSessionMap() {
        return sessionMap;
    }

}
