package com.sjna.teamup.common.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.Arrays;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port:#{6379}}")
    private Integer port;

    @Value("${spring.data.redis.password:#{null}}")
    private String password;

    @Value("${spring.redis.sentinel.master:#{null}}")
    private String master;

    @Value("${spring.redis.sentinel.nodes:#{null}}")
    private String nodes;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisConfiguration redisConfiguration;

        if(StringUtils.isEmpty(nodes)) {
            RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
            standaloneConfig.setHostName(host);
            standaloneConfig.setPort(port);
            if(!StringUtils.isEmpty(password)) {
                standaloneConfig.setPassword(RedisPassword.of(password));
            }
            redisConfiguration = standaloneConfig;
        }else {
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
            sentinelConfig.master(master);
            if(!StringUtils.isEmpty(password)) {
                sentinelConfig.setPassword(RedisPassword.of(password));
            }
            Arrays.asList(nodes.split(",")).forEach(node -> {
                String[] nodeDetails = node.split(":");
                sentinelConfig.addSentinel(new RedisNode(nodeDetails[0], Integer.parseInt(nodeDetails[1])));
            });
            redisConfiguration = sentinelConfig;
        }

        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return redisTemplate;
    }

}
