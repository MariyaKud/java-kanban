package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * Ячейка сетки занятости - хранит метку времени кратную заданному минимальному отрезку в минутах
 */
public final class ItemGrid {
    private final int year;
    private final int dayOfYear;
    private final int minutesOfDay;

    public ItemGrid(int year, int dayOfYear, int minutesOfDay) {
        this.year = year;
        this.dayOfYear = dayOfYear;
        this.minutesOfDay = minutesOfDay;
    }

    public int getYear() {
        return year;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getMinutesOfDay() {
        return minutesOfDay;
    }

    @Override
    public int hashCode() {
        LocalTime time = LocalTime.MIN.plusMinutes(minutesOfDay);
        LocalDate date = LocalDate.ofYearDay(getYear(), getDayOfYear());

        return LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC).hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemGrid item = (ItemGrid) o;
        return getMinutesOfDay() == item.getMinutesOfDay() &&
                getDayOfYear() == item.getDayOfYear() &&
                getYear() == item.getYear();
    }
}

