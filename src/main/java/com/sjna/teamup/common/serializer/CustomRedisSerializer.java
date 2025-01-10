package com.sjna.teamup.common.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CustomRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }

        if (value instanceof String) {
            return ((String) value).getBytes();
        }

        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            throw new SerializationException("Could not serialize object: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        String str = new String(bytes);
        try {
            return objectMapper.readValue(str, Object.class);
        } catch (Exception e) {
            return str; // 역직렬화 실패 시 원래 문자열을 반환
        }
    }
}
