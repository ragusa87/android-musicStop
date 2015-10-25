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
import android.media.AudioManager;
import android.util.Log;

public class VolumeHelper {
    private static AudioManager audio;

    /**
     * Get the music manager
     *
     * @param context Context to get AudioManager service
     */
    private static void init(Context context) {
        if (audio == null)
            audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Get the media volume
     *
     * @param c Context
     * @return Volume
     */
    public static synchronized int getMediaVolume(Context c) {
        init(c);
        return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * Set media volume
     *
     * @param c      Context
     * @param volume Volume
     */
    public static synchronized void setMediaVolume(Context c, int volume) {
        init(c);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    /**
     * Mute the volume in a loop.
     *
     * @param c Context
     */
    public static synchronized void muteMediaVolume(Context c) {
        final int STEP = 5;
        final int v = getMediaVolume(c);
        int z = v;
        int ratio = v / STEP;
        if (v >= STEP) {
            for (int i = 0; i < STEP && v >= STEP; i++) {
                z -= ratio;
                setMediaVolume(c, z);
                sleep(500);
            }
        }
        setMediaVolume(c, 0);
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Log.w("Volume", "Cannot sleep", e);
        }
    }
}