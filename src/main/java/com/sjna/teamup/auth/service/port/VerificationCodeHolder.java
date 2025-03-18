package com.sjna.teamup.auth.service.port;

import com.sjna.teamup.common.service.port.RandomDigitsHolder;
import com.sjna.teamup.common.service.port.UuidHolder;

public interface VerificationCodeHolder {

    String createEmailVerificationCode(UuidHolder uuidHolder);
    String createPhoneVerificationCode(int count, RandomDigitsHolder randomDigitsHolder);

}
