package personal.streaming.application.common.value;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public record DayRange(
        LocalDate start,
        LocalDate end
) {
    public enum Mode {
        MONTH {
            @Override
            DayRange getRange(LocalDate date) {
                return DayRange.ofMonth(date);
            }
        },
        WEEK {
            @Override
            DayRange getRange(LocalDate date) {
                return DayRange.ofWeek(date);
            }
        },
        DAY {
            @Override
            DayRange getRange(LocalDate date) {
                return DayRange.ofDay(date);
            }
        };

        abstract DayRange getRange(LocalDate date);
    }

    public static DayRange fromMode(LocalDate date, String range) {
        Mode modeValue = Mode.valueOf(range.toUpperCase());
        return modeValue.getRange(date);
    }

    public static DayRange ofDay(final LocalDate date) {
        return new DayRange(date, date);
    }

    public static DayRange ofWeek(final LocalDate date) {
        LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return new DayRange(start, end);
    }

    public static DayRange ofMonth(final LocalDate date) {
        LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        return new DayRange(start, end);
    }
}