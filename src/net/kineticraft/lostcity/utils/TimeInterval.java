package net.kineticraft.lostcity.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Represents calendar time intervals.
 * Created by Kneesnap on 7/26/2017.
 */
@AllArgsConstructor @Getter
public enum TimeInterval {
    SECOND("s", TimeUnit.SECONDS, Calendar.SECOND),
    MINUTE("min", TimeUnit.MINUTES, Calendar.MINUTE),
    HOUR("hr", TimeUnit.HOURS, Calendar.HOUR_OF_DAY),
    DAY("day", TimeUnit.DAYS, Calendar.DAY_OF_MONTH),
    WEEK("week", 7, Calendar.WEEK_OF_MONTH),
    MONTH("month", 30, Calendar.MONTH),
    YEAR("yr", 365, Calendar.YEAR);

    private String suffix;
    private TimeUnit unit;
    private int interval;
    private int calendarId;

    TimeInterval(String s, TimeUnit unit, int calendar) {
        this(s, unit, (int) TimeUnit.SECONDS.convert(1, unit), calendar);
    }

    TimeInterval(String s, int days, int calendar) {
        this(s, null, (int) TimeUnit.SECONDS.convert(days, TimeUnit.DAYS), calendar);
    }

    public static TimeInterval getByCode(String code) {
        return Arrays.stream(values()).filter(ti -> ti.getSuffix().startsWith(code.toLowerCase())).findFirst().orElse(SECOND);
    }

    /**
     * Get the current time unit value for this interval.
     * @return unit
     */
    @SuppressWarnings("MagicConstant")
    public int getValue() {
        return Calendar.getInstance().get(getCalendarId());
    }
}
