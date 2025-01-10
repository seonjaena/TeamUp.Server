package com.sjna.teamup.auth.domain;

import com.sjna.teamup.common.domain.EnumFlag;

public enum VERIFICATION_CODE_TYPE implements EnumFlag {

    EMAIL('E')
    , PHONE('P')
    ;

    private final char flag;

    VERIFICATION_CODE_TYPE(char flag) {
        this.flag = flag;
    }

    @Override
    public Character get() {
        return this.flag;
    }
}
