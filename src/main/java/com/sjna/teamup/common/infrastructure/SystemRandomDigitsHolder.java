package com.sjna.teamup.common.infrastructure;

import com.sjna.teamup.common.service.port.RandomDigitsHolder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class SystemRandomDigitsHolder implements RandomDigitsHolder {
    @Override
    public String random(int count) {
        if(count <= 0) {
            return "";
        }
        return RandomStringUtils.randomNumeric(count);
    }
}
