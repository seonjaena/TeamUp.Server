package com.sjna.teamup.auth.infrastructure;

import com.sjna.teamup.auth.service.port.VerificationCodeHolder;
import com.sjna.teamup.common.service.port.RandomDigitsHolder;
import com.sjna.teamup.common.service.port.UuidHolder;
import org.springframework.stereotype.Component;

@Component
public class SystemVerificationCodeHolder implements VerificationCodeHolder {

    @Override
    public String createEmailVerificationCode(UuidHolder uuidHolder) {
        return uuidHolder.random().replace("-", "");
    }

    @Override
    public String createPhoneVerificationCode(int count, RandomDigitsHolder randomDigitsHolder) {
        return randomDigitsHolder.random(count);
    }
}
