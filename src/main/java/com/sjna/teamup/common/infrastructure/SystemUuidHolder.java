package com.sjna.teamup.common.infrastructure;

import com.sjna.teamup.common.service.port.UuidHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SystemUuidHolder implements UuidHolder {
    @Override
    public String random() {
        return UUID.randomUUID().toString();
    }
}
