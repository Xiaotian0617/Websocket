package com.forotc.topcoin.service.impl;

import com.forotc.topcoin.service.Sender;
import com.forotc.topcoin.service.WorkerForRedisImpl;
import com.forotc.topcoin.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/3/11 16:32
 */
@Slf4j
//@Service
public class RedisHashWsServiceImpl implements WsService {
    /**
     * 定义redis 中的前缀，用于防止冲突
     */
    public static String UID_PREFIX = "market-ws_uid_";
    public static String ONLY_KEY_PREFIX = "market-ws_only_key_";
    public static ConcurrentMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    Sender sender;

    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    HashOperations<String, String, Object> opsForHash;

    @PostConstruct
    public void init() {
        opsForHash = redisTemplate.opsForHash();
    }

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

        oks.forEach(s -> {
            opsForHash.put(getPrefixedOnlyKeyKey(s),uid,"");
        });
        redisTemplate.opsForValue().set(getPrefixedUserKey(uid), keys);
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
            String key = getPrefixedUserKey(uid);
            String oks = redisTemplate.opsForValue().get(key);
            List<String> okList = checkOnlyKeys(oks);

            // 取消订阅
            unSubscriptByUIDs(okList, uid);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(">>> 用户订阅时发生异常，用户id:{},异常信息:{}", uid, e.getMessage());
            return false;
        }
        return true;
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
            String key = getPrefixedUserKey(uid);
            String oks = redisTemplate.opsForValue().get(key);
            List<String> okList = checkOnlyKeys(oks);
            // 如果之前订阅过，就取消订阅
            unSubscriptByUIDs(okList, uid);

            // 当传入的参数为空时,表示取消所有订阅
            if (isBlank(keys)) {
                return true;
            }

            // subscript part
            okList = checkOnlyKeys(keys);
            subscriptByUIDs(okList, uid);
            redisTemplate.opsForValue().set(getPrefixedUserKey(uid), keys);
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
                    long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                        long cnt = opsForHash.delete(getPrefixedOnlyKeyKey(o),  uid);
                        return cnt;
                    });
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
                long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                    opsForHash.put(getPrefixedOnlyKeyKey(o), uid,"");
                    log.trace(">>>>>>>set key:{}", getPrefixedOnlyKeyKey(o));
//                    RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
//                    long cnt = connection.rPush(serializer.serialize(getPrefixedOnlyKeyKey(o)),serializer.serialize(uid)
//                            );
                    return 0L;
                });
            } catch (Exception e) {
                log.error(">>> 用户订阅时发生异常，用户id:{},正在订阅:{},异常信息:{}", uid, o, e.getMessage());
            }
        });

        return true;
    }

    private String getPrefixedUserKey(String key) {
        return UID_PREFIX + key;
    }

    private String getPrefixedOnlyKeyKey(String key) {
        return ONLY_KEY_PREFIX + key;
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
        log.debug("tx: {},execute: {}",tx,i);
    }

    @Override
    public ConcurrentMap<String, Session> getSessionMap() {
        return sessionMap;
    }

}
