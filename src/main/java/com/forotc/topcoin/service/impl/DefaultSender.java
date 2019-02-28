package com.forotc.topcoin.service.impl;

import com.alibaba.fastjson.JSON;
import com.forotc.topcoin.service.Sender;
import com.forotc.topcoin.web.api.po.FlushRec;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

/**
 * Default sender for market-ws
 * @author Asin Liu
 * @since  1.0
 * @version 1.0
 */
@Slf4j
public class DefaultSender implements Sender {
    @Override
    public void sendMessage(Map<String,Object> message, Map<String, Session> userSessions) {

        if (userSessions != null && !userSessions.isEmpty()) {
/*            userIds.forEach(o->{
                log.info(o);
                Session session = userSessions.get(o);
                if (null != session && session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(JSON.toJSONString(message.getData()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO :
                    }
                }
            });*/
        }
    }
}
