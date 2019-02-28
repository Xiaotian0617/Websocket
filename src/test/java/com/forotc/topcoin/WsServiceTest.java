package com.forotc.topcoin;

import com.forotc.topcoin.service.WsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/2/3 15:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WsServiceTest {

    @Autowired
    WsService wsService;

    @Test
    public void subscriptTest() {
        String uid = "uid03";
        String keys = "key1,key2,key03,key0";
        System.out.println(">>>>>>>>>>>   subscript   uid : " + uid + "  |   keys :  " + keys);
        boolean flag = wsService.subscript(uid, keys);
        System.out.println(">>>>>>>>>>>   subscript   results  :   " + flag);

    }

    @Test
    public void unSubscriptTest() {
        String uid = "uid01";
        System.out.println(">>>>>>>>>>>   unSubscript   uid :" + uid);
        boolean flag = wsService.unSubscript(uid);
        System.out.println(">>>>>>>>>>>   unSubscript   results  :   " + flag);
    }

    @Test
    public void reSubscriptTest() {
        String uid = "uid01";
        String keys = "key1,key2,key03,key0";
        System.out.println(">>>>>>>>>>>   reSubscript   uid : " + uid + "  |   keys :  " + keys);
        boolean flag = wsService.reSubscript(uid, keys);
        System.out.println(">>>>>>>>>>>   reSubscript   results  :   " + flag);
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void sendMsg(){
        long startTime=System.currentTimeMillis();
        System.out.println("测试耗时：");
        long endTime=System.currentTimeMillis();
        float excTime=(float)(endTime-startTime)/1000;
        System.out.println("执行时间："+excTime+"s////"+endTime);
    }

}
