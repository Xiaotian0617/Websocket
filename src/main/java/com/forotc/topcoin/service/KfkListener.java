package com.forotc.topcoin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Listener for market data from CalcCenter
 */
@Slf4j
@Component
public class KfkListener {

    @Resource
    ObjectMapper objectMapper;
    @Resource
    WsService wsService;

    @KafkaListener(topics = "${market-ws.topic}",groupId = "${market-ws.group}")
    public void listenMarket(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                if (message instanceof String) {
                    String msg = (String)message;
                    List<Map<String,Object>> data = objectMapper.readValue(msg,List.class);
                    /*if (!data.isEmpty()) {
                        Map<String, Object> item = data.get(0);
                        String itemStr = objectMapper.writeValueAsString(item);
                        System.out.println(itemStr);
                    }
                    System.out.println(">>>>>"+ data.size());*/

                    if (log.isDebugEnabled()) {
                        Object obj = data.get(0).get("key"); // hangqing
                        String dataType = obj == null?"[Market Value]":"[Quotes]";
                        log.debug("get data from kafka,type:{}, size: {}",dataType, data.size() );
                    }
                    data.forEach(map->{
                        map.put("msgType","market");
                    });
                    log.trace("接收到的数据  :{}",data);
                    String tx =  System.currentTimeMillis()+":";
                    wsService.process(tx,data);
                }else
                {
                    log.error("Date type error, please check the message format.");
                }
            } catch (Throwable e) {
                log.error("异常消息:{}, 异常:{}",message.toString(),e);
            }
        }
    }

    @KafkaListener(topics = "${depth-ws.topic}",groupId = "${depth-ws.group}")
    public void listenDepth(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                if (message instanceof String) {
                    String msg = (String)message;
                    List<Map<String,Object>> data = objectMapper.readValue(msg,List.class);
                    data.forEach(map->{
                        map.put("msgType","depth");
                    });
                    log.debug("接收到的数据  :{}",data);
                    String tx =  System.currentTimeMillis()+":";
                    wsService.process(tx,data);
                }else {
                    log.error("Date type error, please check the message format.");
                }
            } catch (Throwable e) {
                log.error("异常消息:{}, 异常:{}",message.toString(),e);
            }
        }
    }
}
