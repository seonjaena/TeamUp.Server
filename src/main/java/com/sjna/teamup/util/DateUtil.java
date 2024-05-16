package com.sjna.teamup.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;

public class DateUtil {

    public static boolean isExistDate(LocalDate date) {

        try {
            date.getYear(); // 년도 접근 시 유효성 검사
            date.getMonthValue(); // 월 접근 시 유효성 검사
            date.getDayOfMonth(); // 일자 접근 시 유효성 검사
        } catch (DateTimeException e) {
            // 유효하지 않은 날짜인 경우 예외 발생
            return false;
        }

        // 윤년이 아닌 해의 2월 29일인지 확인
        if (date.getMonthValue() == 2 && date.getDayOfMonth() == 29 && !date.isLeapYear()) {
            return false;
        }

        return true;
    }

    public static boolean isProperDateRange(LocalDate date, LocalDate start, LocalDate end) {

        // 시작 날짜와 끝 날짜가 유효한지 확인
        if(start.isAfter(end)) {
            throw new IllegalArgumentException(String.format("Start Date is late than end date. sDate=%s, eDate=%s", start.toString(), end.toString()));
        }

        // 주어진 날짜가 시작 날짜와 끝 날짜 사이에 있는지 확인
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public static boolean isOlderThanOrEqual(LocalDate birthDate, int age) {
        LocalDate now = LocalDate.now();

        int userAge = now.minusYears(birthDate.getYear()).getYear(); // (1)

        if (birthDate.plusYears(userAge).isAfter(now)) {
            userAge = userAge - 1;
        }

        return userAge >= age;
    }

}
