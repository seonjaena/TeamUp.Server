package com.sjna.teamup.user.domain;

import com.sjna.teamup.common.domain.EnumConverter;
import com.sjna.teamup.common.domain.EnumFlag;

public enum USER_STATUS implements EnumFlag<USER_STATUS> {

    NONE('N')
    , ACTIVE('A')
    , DELETED('D')
    ;

    private final char flag;

    USER_STATUS(char flag) {
        this.flag = flag;
    }

    @Override
    public Character getFlag() {
        return this.flag;
    }

    public static class Converter extends EnumConverter<USER_STATUS> {
        public Converter() {
            super(USER_STATUS.class);
        }
    }
}