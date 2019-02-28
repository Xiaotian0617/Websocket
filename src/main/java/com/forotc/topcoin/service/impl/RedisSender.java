package com.forotc.topcoin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forotc.topcoin.service.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
//@Component
public class RedisSender implements Sender {

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Override
    public void sendMessage(Map<String, Object> message, Map<String, Session> userSessions) {
        Object obj = message.get("key");
        if (obj == null) {
            obj = message.get("onlyKey");
        }
        if (obj == null) {
            return;
        }

        String onlyKey = (String) obj;
        String msgType = (String)message.get("msgType");
        String key = null;
        if ("market".equals(msgType)){
            key = RedisBasedWsServiceImpl.ONLY_KEY_MARKET_PREFIX + onlyKey;
        }else if ("depth".equals(msgType)){
            key = RedisBasedWsServiceImpl.ONLY_KEY_DEPTH_PREFIX + onlyKey;
        }
        // long size = redisTemplate.opsForList().size(key);
//        List<String> userIds = redisTemplate.opsForList().range(key, 0, Integer.MAX_VALUE);
        Set<Object> userIds = redisTemplate.opsForHash().keys(key);
        if (userIds == null || userIds.size() < 1) {
            return;
        }

        //String [] added = onlyKey.split("_");
        ///message.put("exch",added[0]);   //
        ///message.put("sym",added[1]);
        //message.put("unit",added[2]);
        //市场的唯一标识 例如：Okex_ETH_BTC
        //private String key;//onlyKey;
        //private String exch;//exchange; //交易所
        //private String symbol;//e.g. btc,eth...
        //private String unit;//兑换货币单位 BTC/USD 中的 USD

        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error(">>> 发送前序列化数据时产生错误，MSG:{}",e.getMessage());
            return;
        }
        String msg = jsonStr;
        userIds.forEach(o -> {
            String os = (String) o;
            log.debug(os);
            Session session = userSessions.get(os);
            if (null != session && session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(" session.getBasicRemote().sendText ERROR ",e);
                }
            }
        });
    }
}
