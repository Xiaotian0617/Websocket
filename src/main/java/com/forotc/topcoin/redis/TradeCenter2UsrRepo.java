package com.forotc.topcoin.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

@Repository
public class TradeCenter2UsrRepo {
	
	 @Autowired
	 RedisTemplate redisTemplate;  //;; =redisConfig.redisTemplate();s
	 
	 @Autowired
	 StringRedisTemplate strTemplate ; //  = redisConfig.stringRedisTemplate();
	 
	 public void  testRedis() {
		 
		System.out.println(strTemplate);
		 
		HashOperations<String, String, String> hashOperations = strTemplate.opsForHash();
		ListOperations<String,String> var= strTemplate.opsForList();
		
		ValueOperations values = strTemplate.opsForValue();
	    values.set("01", "Joe");
//	    values.set("02", "John");
//	    values.set("03", "tt");
		
		hashOperations.put("myHash", "myhash", "value");
		hashOperations.put("myHash", "myhash1", "value1");
		hashOperations.put("myHash", "myhash2", "value2");
		hashOperations.put("myKey", "key", "value");
		hashOperations.put("myKey",  "key1", "keyvalue1");
		hashOperations.put("ttHash", "key1", "keyvalue1");
		
		String yy = hashOperations.get("yyhash", "key");
		
//		String str = var.rightPop("products");
//		boolean b1 = redisTemplate.hasKey("products");
//		boolean b2 = redisTemplate.hasKey("test");
		
//		redisTemplate.
		System.out.println(var.getOperations() + "yy:" + yy);
		System.out.println("yzy"  + hashOperations.get("myHash", "myhash") + ":ttt!");
		System.out.println("yzy"  + hashOperations.get("myHash", "myhash1") + ":ttt!");
	 }


}
