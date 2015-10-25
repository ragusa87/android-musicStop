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
import android.os.SystemClock;
import android.view.KeyEvent;

/**
 * Useful procedures to stop the music
 * <ul>
 * <li>- Press the MEDIA_PLAY_PAUSE key</li>
 * <li>- Press the MEDIA_STOP key</li>
 * <li>- Plug and un-plug some virtual headset</li>
 * <li>- Un-Plug and plug some virtual headset</li>
 * <li>- Use the ACTION_AUDIO_BECOMING_NOISY functionality (See
 * #android.media.AudioManager)</li>
 * </ul>
 *
 * @author Constantin Laurent
 */
public class StopHelper {
    /**
     * Create and send a KeyEvent through the AudioService, fallback to a simple
     * intent broadcast if necessary.On KitKat, the event should be send by the
     * service, otherwise it's not working with SELinux enforced mode.
     *
     * @param c       Context
     * @param action  KeyEvent.ACTION_*
     * @param keycode KeyEvent.KEYCODE_*
     */
    private static void sendKey(Context c, int action, int keycode) {
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent keyEvent = new KeyEvent(eventTime, eventTime, action, keycode,
                0);

        AudioManager audioManager = (AudioManager) c
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.dispatchMediaKeyEvent(keyEvent);

    }

    /**
     * Send two KeyEvent, one with Action_down and then one with Action_up
     *
     * @param c       Context
     * @param keyCode See KeyEvent.KEYCODE_
     */
    private static void sendKey(Context c, int keyCode) {

        sendKey(c, KeyEvent.ACTION_DOWN, keyCode);
        sendKey(c, KeyEvent.ACTION_UP, keyCode);
    }

    private static void sendStopKey(Context c) {
        sendKey(c, KeyEvent.KEYCODE_MEDIA_STOP);
    }

    private static void sendPlayPauseKey(Context c) {
        sendKey(c, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    /**
     * Stop the music with the selected method
     *
     * @param c      Context
     * @param method The selected method (see array.xml)
     * @return True if the method is known, false otherwise
     */
    public static boolean stopMusic(Context c, Method method) {
        switch (method) {
            default:
                return false;

            case STOP:
                StopHelper.sendStopKey(c);
                break;
            case PLAY_PAUSE:
                StopHelper.sendPlayPauseKey(c);
                break;
            case MUTE: // Mute
                VolumeHelper.setMediaVolume(c, 0);
                break;
        }
        return true;
    }

    public enum Method {STOP, PLAY_PAUSE, MUTE}


}