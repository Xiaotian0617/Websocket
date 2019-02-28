package com.forotc.topcoin.conf;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Redis 的配置.
 *
 * @author Asin Liu
 * @since 1.0
 * @verison 1.0
 *
 *
 * Note:
 * Redis 的配置，当前支持直接使用redis模板及spring cache接口.
 * 当使用cache时建议使用JSR107标准接口。
 */

@Configuration
@EnableCaching
public class RedisCacheConf {

    /**
     * 当使用cache时，对key做预处理.
     * 在序列化之前对key做定制化处理防止和其它的key重复
     * 当前使用包名+类名+方法名+方法参数做为key，也就是说
     * 同一个方法的不同的方法参数会被系统认为是不同的key.
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator(){
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder(128);
            sb.append(target.getClass().getName()).append("#");
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append("_").append(obj.toString());
            }
            return sb.toString();
        };
    }


    /**
     * 当使用cache时，缓存的数据默认缓存时间为60 seconds.
     * @param redisTemplate
     * @return
     */
    /*@Bean
    public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // 设置默认过期时间为60秒
        cacheManager.setDefaultExpiration(60);
        return cacheManager;
    }
*/

    /**
     * 当使用redisTemplate 时，对value的序列化使用GenericJackson2JsonRedisSerializer()
     * 有关使用此序列化器的详细信息请访问：https://github.com/spring-projects/spring-data-redis/pull/145
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
        //StringRedisTemplate template = new StringRedisTemplate(factory);
        //Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //ObjectMapper om = new ObjectMapper();
        //om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //jackson2JsonRedisSerializer.setObjectMapper(om);
        final RedisTemplate<String,Object> template = new RedisTemplate<>();
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setConnectionFactory(factory);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
