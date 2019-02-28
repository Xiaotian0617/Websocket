package com.forotc.topcoin.web.svc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/*
 * Intended for interacting with redis, actually not getting used right now,
 * kept here might to be used later.
 */

@Component
@Slf4j
public class RedisSvc {

	@Autowired
	StringRedisTemplate strTemplate;

	public boolean updateHashKey(String uid, String tcid, String flag) {
		HashOperations<String, String, String> hashOps = strTemplate.opsForHash();
		try {
			hashOps.put("tc2usr-registered", uid + "-" + tcid, flag);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

}
