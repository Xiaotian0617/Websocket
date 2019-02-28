//package com.forotc.topcoin.web.api;
//
//import com.alibaba.fastjson.JSON;
//import com.forotc.topcoin.web.api.po.FlushRec;
//import com.forotc.topcoin.web.api.po.RegisterUser;
//import com.forotc.topcoin.web.api.po.RegisterUserKeys;
//import com.forotc.topcoin.web.svc.RedisSvc;
//import com.forotc.topcoin.web.svc.WsSvc;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//@Slf4j
//public class WsApiController {
//
//    @Autowired
//    RedisSvc redisSvc;
//
//    @Autowired
//    WsSvc wsSvc;
//
//    @RequestMapping(value = "/registerkeys", method = RequestMethod.POST, consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public ResponseEntity registerKeys(@RequestBody RegisterUserKeys ruk) {
//        log.info("registerKeys:" + this.toString() + ":" + ruk.getUid() + ":" + ruk.getKeys());
//        if (wsSvc.updateRegisteredKeys(ruk.getUid(), ruk.getKeys(), true))
//            return new ResponseEntity(HttpStatus.OK);
//        else
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @RequestMapping(value = "/unregisterkeys", method = RequestMethod.POST, consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public ResponseEntity unregisterKeys(@RequestBody RegisterUserKeys ruk) {
//        log.info("registerKeys:" + ruk.getUid() + ":" + ruk.getKeys());
//        if (wsSvc.updateRegisteredKeys(ruk.getUid(), ruk.getKeys(), false))
//            return new ResponseEntity(HttpStatus.OK);
//        else
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//
//    @RequestMapping(value = "/flushws", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public HttpStatus flushws(@RequestBody List<FlushRec> frs) {
//        log.info("flushws start");
//        for (FlushRec fr : frs) {
////            wsSvc.flushWs(fr.getTcid(), fr.getData());
//            wsSvc.flushWs(fr.getTcid(), JSON.toJSONString(fr.getData()));
//        }
//        log.info("flushws end");
//        return HttpStatus.OK;
//    }
//
//    /*
//     * Below method not used now.
//     */
//    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public ResponseEntity register(@RequestBody RegisterUser ru) {
//        if (redisSvc.updateHashKey(ru.getUid(), ru.getTcid(), "true"))
//            return new ResponseEntity(HttpStatus.OK);
//        else
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @RequestMapping(value = "/unregister", method = RequestMethod.POST, consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public ResponseEntity unregister(@RequestBody RegisterUser ru) {
//        if (redisSvc.updateHashKey(ru.getUid(), ru.getTcid(), "false"))
//            return new ResponseEntity(HttpStatus.OK);
//        else
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}