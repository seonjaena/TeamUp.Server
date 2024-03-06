package com.sjna.teamup.entity.enums;

public enum VERIFICATION_CODE_TYPE implements EnumFlagable<VERIFICATION_CODE_TYPE> {

    EMAIL('E', "EMAIL"),
    PHONE('P', "PHONE")
    ;

    private final char flag;
    private final String text;

    VERIFICATION_CODE_TYPE(char flag, String text) {
        this.flag = flag;
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public Character get() {
        return this.flag;
    }

    public static class Converter extends EnumConverter<VERIFICATION_CODE_TYPE> {
        public Converter() {
            super(VERIFICATION_CODE_TYPE.class);
        }
    }
}
