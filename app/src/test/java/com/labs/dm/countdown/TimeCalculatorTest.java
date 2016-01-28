package com.labs.dm.countdown;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by daniel on 2016-01-27.
 */
public class TimeCalculatorTest {

    private TimeCalculator tc = new TimeCalculator();
    private final long ONE_DAY_IN_MILLIS = 86400000L;
    private final long ONE_HOUR_IN_MILLIS = 3600000L;

    @Test
    public void testCalculate1() {
        long now = Calendar.getInstance().getTimeInMillis();
        long tomorrow = now + ONE_DAY_IN_MILLIS;

        TimeCalculator.TimeContainer res = tc.calculate(tomorrow, now, 0);
        assertEquals(1, res.days);
        assertEquals(0, res.hours);
        assertEquals(0, res.weeks);
    }

    @Test
    public void testCalculate3() {
        long now = Calendar.getInstance().getTimeInMillis();
        long future = now + 7 * ONE_DAY_IN_MILLIS + 4 * ONE_HOUR_IN_MILLIS;

        TimeCalculator.TimeContainer res = tc.calculate(future, now, 0);
        assertEquals(7, res.days);
        assertEquals(4, res.hours);
        assertEquals(0, res.weeks);
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void testCalculate2() throws Exception {
        long now = Calendar.getInstance().getTimeInMillis();
        tc.calculate(now - 1, now, 0);
    }
}