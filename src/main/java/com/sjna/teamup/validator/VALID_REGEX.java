package com.sjna.teamup.validator;

public enum VALID_REGEX {

    USER_ID("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"),
    USER_PW("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$"),
    PHONE("^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$"),
    USER_NAME("^[가-힣]{2,4}$"),
    USER_NICKNAME("^(?!_)(?=.*[a-zA-Z0-9가-힣_])[a-zA-Z0-9가-힣_]{2,16}(?<!_)$"),
    ;

    private final String regexp;

    VALID_REGEX(String regexp) {
        this.regexp = regexp;
    }

    public String getRegexp() {
        return this.regexp;
    }

}
