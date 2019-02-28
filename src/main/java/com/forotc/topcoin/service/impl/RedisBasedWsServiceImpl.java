package com.forotc.topcoin.service.impl;

import com.forotc.topcoin.service.Sender;
import com.forotc.topcoin.service.WorkerForRedisImpl;
import com.forotc.topcoin.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


/**
 * 默认的WsService 实现
 * <p>
 * 当前使用订阅的实现是使用redis管理
 *
 * @author Asin Liu
 * @version 1.0
 * @since 1.0
 */

@Slf4j
@Service
public class RedisBasedWsServiceImpl implements WsService {

    /**
     * 定义redis 中的前缀，用于防止冲突
     */
    public static String UID_MARKET_PREFIX = "market-ws_uid_";
    public static String UID_DEPTH_PREFIX = "depth-ws_uid_";
    public static String ONLY_KEY_MARKET_PREFIX = "market-ws_only_key_";
    public static String ONLY_KEY_DEPTH_PREFIX = "depth-ws_only_key_";
    public static String ONLY_KEY_DEPTH_SUFFIX = "|depth";
    protected static ConcurrentMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    protected static CopyOnWriteArrayList<String> failureUid = new CopyOnWriteArrayList<>();

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    Sender sender;

    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * 用户订阅
     * 1. 持久化用户与订阅的关系, 此部分使用了前缀!
     * 2. 对于每一个key 建立对应列表
     *
     * @param uid  用户id
     * @param keys 订阅的only Key 列表组成的字符串
     * @return
     */
    @Override
    public boolean subscript(String uid, String keys) {
        if (isBlank(uid) || isBlank(keys)) {
            log.error(">>> 订阅参数有误，用户id:{},订阅的only keys:{},每一个only key 都不能为空。");
            return false;
        }
        List<String> oks = checkOnlyKeys(keys);

        // subscript message
        subscriptByUIDs(oks, uid);
        redisTemplate.opsForValue().set(getPrefixedUserKey(keys,uid), keys);

        /*
        try {
            String key = getPrefixedUserKey(uid);
            redisTemplate.opsForValue().set(key, keys);
            String[] ks = keys.split(",");
            for (int i = 0; i < ks.length; i++) {
                String onlyKey = ks[i];
                if (isBlank(onlyKey)) {
                    log.error(">>> 订阅参数有误，用户id:{},订阅的only keys:{},每一个only key 都不能为空。");
                    continue;
                }
                long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                    RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                    long cnt = connection.rPush(serializer.serialize(onlyKey),serializer.serialize(uid));
                    return cnt;
                });
            }

        } catch (Exception e) {
            log.error(">>> 用户订阅时发生异常，用户id:{},订阅的only keys:{},异常信息:{}",uid,keys,e.getMessage());
            return false;
        }*/
        return true;
    }


    /**
     * 删除当前用户的订阅消息
     *
     * @param uid 用户的id
     * @return
     */
    @Override
    public boolean unSubscript(String uid) {
        if (isBlank(uid)) {
            log.error(">>> 订阅参数有误，用户id:{},不能为空。", uid);
            return false;
        }
        try {
            removeAllSubscript(uid);
        } catch (Exception e) {
            log.error(">>> 用户订阅时发生异常，用户id:{},异常信息:{}", uid, e.getMessage());
            return false;
        }
        return true;
    }

    private void removeAllSubscript(String uid) {
        String marketKey = getPrefixedUserKey(uid);
        String marketOks = redisTemplate.opsForValue().get(marketKey);
        String depthKey = getPrefixedUserKey(ONLY_KEY_DEPTH_SUFFIX,uid);
        String depthOks = redisTemplate.opsForValue().get(depthKey);
        List<String> marketOkList = checkOnlyKeys(marketOks);
        List<String> depthOkList = checkOnlyKeys(depthOks);

        // 取消订阅
        unSubscriptByUIDs(marketOkList, uid);
        unSubscriptByUIDs(depthOkList, uid);
        redisTemplate.delete(marketKey);
        redisTemplate.delete(depthKey);
    }

    /**
     * 重新订阅
     *
     * @param uid  用户id
     * @param keys 订阅的keys
     * @return reSubscript status. true or false.
     */
    @Override
    public boolean reSubscript(String uid, String keys) {
        //if (isBlank(uid) || isBlank(keys)) {
        if (isBlank(uid)) {
            log.error(">>> 重新订阅失败，传入的参数有误，用户id:{},订阅的only keys:{},每一个only key 都不能为空。");
            return false;
        }
        try {
            // unsubscript part
            removeAllSubscript(uid);

            // 当传入的参数为空时,表示取消所有订阅
            if (isBlank(keys)) {
                return true;
            }

            // subscript part
            List<String> okList = checkOnlyKeys(keys);
            subscriptByUIDs(okList, uid);
            redisTemplate.opsForValue().set(getPrefixedUserKey(keys,uid), keys);
        } catch (Exception e) {
            log.error(">>> 用户订阅时发生异常，用户id:{},异常信息:{}", uid, e.getMessage());
            return false;
        }
        return true;
    }

    private void unSubscriptByUIDs(List<String> okList, String uid) {
        if (null != okList && !okList.isEmpty()) {
            okList.forEach(o -> {
                try {
                    redisTemplate.opsForList().remove(getPrefixedOnlyKeyKey(o), 0, uid);
//                        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
//                        long cnt = connection.lRem(serializer.serialize(getPrefixedOnlyKeyKey(o)), -1, serializer.serialize(uid));
                } catch (Exception e) {
                    log.error(">>> 用户取消订阅时发生异常，用户id:{},正在取消订阅:{},异常信息:{}", uid, o, e.getMessage());
                }
            });
        }
    }

    /**
     * 用户订阅
     *
     * @param okList 订阅的列表
     * @param uid    用户id
     */
    private boolean subscriptByUIDs(List<String> okList, String uid) {
        if (null == okList || okList.isEmpty()) {
            log.error(">>> 订阅参数有误，用户id:{},订阅的每一个only key 都不能为空。");
            return false;
        }
        okList.forEach(o -> {
            try {
                long cnt = redisTemplate.opsForList().leftPush(getPrefixedOnlyKeyKey(o), uid);
                log.trace(">>>>>>>set key:{}", getPrefixedOnlyKeyKey(o));
//                    RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
//                    long cnt = connection.rPush(serializer.serialize(getPrefixedOnlyKeyKey(o)),serializer.serialize(uid)
//                            );
            } catch (Exception e) {
                log.error(">>> 用户订阅时发生异常，用户id:{},正在订阅:{},异常信息:{}", uid, o, e.getMessage());
            }
        });

        return true;
    }

    private String getPrefixedUserKey(String uid) {
        return getPrefixedUserKey("",uid);
    }

    private String getPrefixedUserKey(String keys,String uid) {
        if (keys.contains(ONLY_KEY_DEPTH_SUFFIX)){
            return UID_DEPTH_PREFIX + uid;
        }else {
            return UID_MARKET_PREFIX + uid;
        }
    }

    private String getPrefixedOnlyKeyKey(String key) {
        if (key.contains(ONLY_KEY_DEPTH_SUFFIX)){
            return ONLY_KEY_DEPTH_PREFIX + key;
        }else {
            return ONLY_KEY_MARKET_PREFIX + key;
        }
    }

    private boolean isBlank(String src) {
        return null == src || "".equals(src.trim());
    }

    /**
     *  只有正确的参数才可以进入到系统, 错误的参数将被忽略
     *
     * @param onlyKeys
     * @return
     */
    private List<String> checkOnlyKeys(String onlyKeys) {
        if (isBlank(onlyKeys))
            return null;
        try {
            String[] oks = onlyKeys.split(",");
            return Arrays.stream(oks).filter(o -> !isBlank(o)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(">>> 解析only key时出错，传入的only keys:{}", onlyKeys);
        }
        return null;
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
            Session s = sessionMap.remove(uid);
            s = null;
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
     * 处理计算中心发送来的请求
     * <p>
     * 遍历列表中所有数据，对其中的每一数据在线程池中
     * 取一个线程处理,对处理的的线程池有如下要求：
     * 1. 为了不增加对主线程的干扰，要求线程池的条件允许的范围内尽可能的大，但不要大过frList.size()
     * 2. 有关核心线程与扩展线程的设置在符合业务数量的大小，依据业务的load值来修正,对于frList.size()
     * 稳定的情况下，我们建议将核心线程与最大线程数设置为比较接近的值，以避免重复创建大量
     * 线程的问题。
     * 3.对于需要控制发送速度的应用来说，可以设置较小的核心线程和最大线程数，并设置较大的队列。
     *
     * @param data
     * @return
     */
    @Override
    public void process(String tx, List<Map<String, Object>> data) {
        int i = 0;
        for (Map<String, Object> entry : data) {
            if (null == entry.get("key") && null == entry.get("onlyKey")) {
                log.debug("only key && only key are null");
                continue;
            }
            try {
                i++;
                threadPoolExecutor.execute(new WorkerForRedisImpl(sender, entry, sessionMap));
            } catch (Exception e) {
                log.error(">>> 执行data时发生异常，异常描述:{}, data:{}, ", e.getMessage(), entry);
                //TODO : 是否有补偿机制来处理此异常
            }
        }
        log.debug("tx: {},execute: {}", tx, i);
    }

    @Override
    public ConcurrentMap<String, Session> getSessionMap() {
        return sessionMap;
    }

}
