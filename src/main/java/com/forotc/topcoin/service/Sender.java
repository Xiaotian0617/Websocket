package com.forotc.topcoin.service;

import javax.websocket.Session;
import java.util.Map;

public interface Sender {
    void sendMessage(Map<String,Object> message, Map<String,Session> userSessions);
}
