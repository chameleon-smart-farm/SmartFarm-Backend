package com.smartfarm.chameleon.global.config;

import java.time.Duration;
import java.util.HashMap;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 기본 configuration 생성
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                            .disableCachingNullValues()
                                            .entryTtl(Duration.ofDays(1))
                                            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        // 특정 캐시들의 configuration을 저장할 HashMap
        HashMap<String, RedisCacheConfiguration> configMap = new HashMap<>();

        // weather 캐시 configuration 설정
        configMap.put("weather", RedisCacheConfiguration.defaultCacheConfig()
                                        .entryTtl(Duration.ofMinutes(60)));


       return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory).cacheDefaults(config)
                        .withInitialCacheConfigurations(configMap).build();                        

    }
    
}
