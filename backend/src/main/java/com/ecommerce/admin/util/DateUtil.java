package com.ecommerce.admin.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateUtil() {
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now(DEFAULT_ZONE_ID);
    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    public static ZonedDateTime getCurrentZonedDateTime() {
        return ZonedDateTime.now(DEFAULT_ZONE_ID);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    public static LocalDate getStartOfDay(LocalDate date) {
        return date;
    }

    public static LocalDate getEndOfDay(LocalDate date) {
        return date;
    }

    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusNanos(1);
    }

    public static LocalDate getStartOfWeek() {
        return LocalDate.now(DEFAULT_ZONE_ID).with(DayOfWeek.MONDAY);
    }

    public static LocalDate getEndOfWeek() {
        return LocalDate.now(DEFAULT_ZONE_ID).with(DayOfWeek.SUNDAY);
    }

    public static LocalDate getStartOfMonth() {
        return LocalDate.now(DEFAULT_ZONE_ID).withDayOfMonth(1);
    }

    public static LocalDate getEndOfMonth() {
        LocalDate now = LocalDate.now(DEFAULT_ZONE_ID);
        return now.withDayOfMonth(now.lengthOfMonth());
    }

    public static LocalDate getStartOfYear() {
        return LocalDate.now(DEFAULT_ZONE_ID).withDayOfYear(1);
    }

    public static LocalDate getEndOfYear() {
        LocalDate now = LocalDate.now(DEFAULT_ZONE_ID);
        return now.withDayOfYear(now.lengthOfYear());
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static LocalDate addDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    public static boolean isExpired(LocalDateTime dateTime) {
        return dateTime.isBefore(getCurrentDateTime());
    }

    public static boolean isToday(LocalDate date) {
        return date.equals(getCurrentDate());
    }

    public static boolean isPast(LocalDate date) {
        return date.isBefore(getCurrentDate());
    }

    public static boolean isFuture(LocalDate date) {
        return date.isAfter(getCurrentDate());
    }
}
