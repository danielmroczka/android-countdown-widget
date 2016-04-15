package com.labs.dm.countdown;

import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 2016-01-27.
 */
public class TimeCalculator {

    class TimeContainer {
        public TimeContainer(long hours, long days, long weeks) {
            this.hours = hours;
            this.days = days;
            this.weeks = weeks;
        }

        long hours;
        long days;
        long weeks;
    }

    public TimeContainer calculate(long future, long now) {
        if (future < now) {
            //throw new IllegalArgumentException("Parameter future is older than present!");
        }

        long difference = future - now;

        long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        difference = difference - (days * 86400000L);
        long hours = TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS);

        return new TimeContainer(hours, days, 0);
    }
}
