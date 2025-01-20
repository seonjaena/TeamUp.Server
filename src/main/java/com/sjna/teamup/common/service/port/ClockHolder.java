package com.sjna.teamup.common.service.port;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ClockHolder {

    LocalDateTime getCurrentDateTime();
    LocalDate getCurrentDate();

}
