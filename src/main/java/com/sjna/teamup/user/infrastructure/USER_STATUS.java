package com.sjna.teamup.user.infrastructure;

import com.sjna.teamup.common.domain.EnumConverter;
import com.sjna.teamup.common.domain.EnumFlag;

public enum USER_STATUS implements EnumFlag {

    NORMAL('N')
    , DELETED('D')
    ;

    private final char flag;

    USER_STATUS(char flag) {
        this.flag = flag;
    }

    @Override
    public Character get() {
        return this.flag;
    }

    public static class Converter extends EnumConverter<USER_STATUS> {
        public Converter() {
            super(USER_STATUS.class);
        }
    }
}