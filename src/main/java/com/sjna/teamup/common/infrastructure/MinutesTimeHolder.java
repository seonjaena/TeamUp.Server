package com.sjna.teamup.common.infrastructure;

import com.sjna.teamup.common.service.port.TimeUnitHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MinutesTimeHolder implements TimeUnitHolder {
    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.MINUTES;
    }
}
