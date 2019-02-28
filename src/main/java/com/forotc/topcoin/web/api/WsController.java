package com.forotc.topcoin.web.api;

import com.forotc.topcoin.service.WsService;
import com.forotc.topcoin.web.api.po.RegisterUserKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("ws")
public class WsController {

    @Resource
    WsService wsService;

    /**
     * 重新订阅
     *
     * @param ruk    RegisterUserKeys object.
     * @return       业务状态，true 表示成功，false表示失败
     */
    @RequestMapping(value = "resubscript",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public Object registerKeys(@RequestBody RegisterUserKeys ruk) {
        log.debug("WsController get param uid: {}, keys:{}",  ruk.getUid() , ruk.getKeys());
        if (wsService.reSubscript(ruk.getUid(), ruk.getKeys()))
            return true;
         else
            return false;
    }


    /**
     * 数据中心的调用接口，当数据中心有数据时，调用此接口向客户端推送消息.
     * @param frs
     * @return
     */
//    @PostMapping("flushws")
    @ResponseBody
    public HttpStatus flushws(@RequestBody List<Map<String,Object>> frs) {
        // TODO: 此处的数据是否需要全部log，待以后再处理
        log.info("\r\n flushws, params size is: {}\r\n", frs.size() );
        String tx =  System.currentTimeMillis()+":";
        wsService.process(tx,frs);
        return HttpStatus.OK;
    }
}
