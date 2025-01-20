package com.sjna.teamup.common.infrastructure;

import com.sjna.teamup.common.service.port.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class SystemClockHolder implements ClockHolder {
    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now(Clock.systemUTC());
    }
}
