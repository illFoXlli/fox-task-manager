package com.fox.taskmanager.support;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AppTime {

    private static final Clock UTC_CLOCK = Clock.systemUTC();
    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private AppTime() {
    }

    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(UTC_CLOCK);
    }

    public static String toUtcString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        return UTC_FORMATTER.format(dateTime) + "Z";
    }
}
