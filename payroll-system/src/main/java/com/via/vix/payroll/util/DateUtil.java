package com.via.vix.payroll.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static int ageFromDob(LocalDate dob) {
        if (dob == null) return 0;
        return (int) ChronoUnit.YEARS.between(dob, LocalDate.now());
    }
    public static String todayDateString() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public static String nowTimeString() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
