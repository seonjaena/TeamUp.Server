package com.sjna.teamup.entity.enums;

public enum USER_STATUS implements EnumFlagable<USER_STATUS> {

    NORMAL('N', "NORMAL"),
    DELETED('D', "DELETED");

    private final char flag;
    private final String text;

    private USER_STATUS(char flag, String text) {
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

    public static class Converter extends EnumConverter<USER_STATUS> {
        public Converter() {
            super(USER_STATUS.class);
        }
    }
}