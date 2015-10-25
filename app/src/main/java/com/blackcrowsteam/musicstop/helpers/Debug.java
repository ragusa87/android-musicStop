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

import com.blackcrowsteam.musicstop.BuildConfig;

/**
 * Log only if DEBUG is enabled
 *
 * @author Constantin Laurent
 */
public class Debug {
    private static final String LOG_NAME = "MusicStop";
    private static final Boolean DEBUG = BuildConfig.DEBUG;

    public static class Log {
        public static void e(String msg) {
            if (DEBUG && msg != null)
                android.util.Log.e(LOG_NAME, msg);
        }

        public static void e(String msg, Throwable tr) {
            if (DEBUG && msg != null)
                android.util.Log.e(LOG_NAME, msg, tr);
        }

        public static void v(String msg) {
            if (DEBUG && msg != null)
                android.util.Log.v(LOG_NAME, msg);
        }

        public static void v(String msg, Throwable tr) {
            if (DEBUG && msg != null)
                android.util.Log.v(LOG_NAME, msg, tr);
        }

    }

}