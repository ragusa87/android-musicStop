/*
 * Copyright 2015 Laurent Constantin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.blackcrowsteam.musicstop.helpers;

import android.content.Context;
import android.text.format.DateUtils;

import com.blackcrowsteam.musicstop.R;

import java.text.DecimalFormatSymbols;

/**
 * Helper to display time and duration
 */
public class TimeHelper {
    private static final int ROUND = 900;

    public static CharSequence durationToString(Context context, long milliseconds) {
        String formats[] = context.getResources().getStringArray(R.array.stopwatch_format_set);
        char decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        int formatIndex;
        long seconds, minutes, hours;
        milliseconds += ROUND;
        seconds = milliseconds / 1000;
        minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        hours = minutes / 60;
        minutes = minutes - hours * 60;

        if (minutes < 1 && hours == 0) {
            formatIndex = 0;
        } else if (hours < 1) {
            formatIndex = 1;
        } else {
            formatIndex = 2;
        }
        return String.format(formats[formatIndex], hours, minutes,
                seconds, 0, decimalSeparator);
    }

    public static String getDuration(long milliseconds) {
        milliseconds += ROUND;
        long seconds, minutes, hours;
        seconds = milliseconds / 1000;
        minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        hours = minutes / 60;
        minutes = minutes - hours * 60;
        return String.format("%02dh%02dm%02ds", hours, minutes, seconds);
    }

    public static CharSequence remainingString(long endtime) {
        final long now = System.currentTimeMillis();
        // Force a positive end-time (Displays "in 0 seconds" instead of "200 ms ago")
        final long notNegativeEndtime = Math.max(endtime + ROUND, now);
        return DateUtils.getRelativeTimeSpanString(notNegativeEndtime, now, DateUtils.SECOND_IN_MILLIS);
    }
}
