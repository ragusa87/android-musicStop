package com.blackcrowsteam.musicstop;
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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.blackcrowsteam.musicstop.helpers.TimeHelper;

import java.util.Calendar;

/**
 * Timer. Used to process a countdown.
 * Got a duration and a nullable start time.
 * Duration are relative time in ms.
 * Time are 'Epoch time in ms' since 1 January 1970
 */
public class Timer implements Parcelable {
    // Parcelable
    public static final Parcelable.Creator<Timer> CREATOR
            = new Parcelable.Creator<Timer>() {
        public Timer createFromParcel(Parcel in) {
            return new Timer(in);
        }

        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    // Start time ot the timer in ms. Null if not started
    private Long startTime;
    // Timer's duration in ms
    private long duration;

    /**
     * Construct Timer from parcel
     *
     * @param in Parcel
     */
    private Timer(Parcel in) {
        this(in.readLong());
        boolean hasStartTime = in.readInt() == 1;
        if (hasStartTime) {
            this.startTime = in.readLong();
        } else {
            this.startTime = null;
        }
    }

    public Timer(long duration) {
        this.duration = duration;
    }

    /**
     * Get the current time in ms (Epoch time)
     *
     * @return epoch time in ms
     */
    private static long getTimeNow() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    public static Timer fromSavedInstance(Bundle saved) {
        return saved.getParcelable("parcel");
    }

    /**
     * Get duration in ms
     *
     * @return duration in ms
     */
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Timer is running
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return this.startTime != null && getRemainingDuration() > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Get timer progression. 0 if not started
     *
     * @return progression
     */
    public float getPercent() {
        if (this.startTime == null) {
            return 0;
        }
        final float p = Math.min((float) this.getRemainingDuration() / (float) this.duration, 1);
        return Math.max(p, 0);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeInt(startTime == null ? 0 : 1);
        if (startTime != null) {
            dest.writeLong(startTime);
        }
    }

    /**
     * Get elapsed duration in ms, 0 if not started
     *
     * @return elapsed duration
     */
    private long getElapsedDuration() {

        return this.startTime == null ? 0 : getTimeNow() - this.startTime;
    }

    /**
     * Get remaining duration in ms
     *
     * @return duration ms
     */
    public long getRemainingDuration() {
        long response = this.duration - getElapsedDuration();
        return Math.max(0, response);
    }

    /**
     * To sting as a debug purpose
     *
     * @return string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("Timer of ").append(TimeHelper.getDuration(this.duration));
        if (this.startTime != null) {
            sb.append(String.format(" %02f", this.getPercent() * 100));
            sb.append("%");
            sb.append(" in ");
            sb.append(TimeHelper.getDuration(this.getRemainingDuration()));
        }
        return sb.toString();
    }

    /**
     * Start timer
     */
    public void start() {
        this.startTime = getTimeNow();
    }

    /**
     * Timer has been started
     *
     * @return Timer has been started
     */
    public boolean isStarted() {
        return this.startTime != null;
    }

    /**
     * Stop the timer
     */
    public void stop() {
        this.startTime = null;
    }

    /**
     * Get stop time
     *
     * @return Stop time in ms
     */
    public long getStopTime() {
        if (startTime == null) {
            return getTimeNow();
        }
        return startTime + duration;
    }

    public void save(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("parcel", this);
    }
}
