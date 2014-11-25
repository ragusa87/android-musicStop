/*
 * Copyright 2012 Laurent Constantin <android@blackcrowsteam.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blackcrowsteam.musicstop;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
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
 * 
 */
public class StopHelper {
	/**
	 * Create and send a KeyEvent through the AudioService, failback to a simple
	 * intent broadcast if necessary.On KitKat, the event should be send by the
	 * service, otherwise it's not working with SELinux enforced mode.
	 * 
	 * @param c
	 *            Context
	 * @param action
	 *            KeyEvent.ACTION_*
	 * @param keycode
	 *            KeyEvent.KEYCODE_*
	 */
	private static void sendKey(Context c, int action, int keycode) {
		long eventtime = SystemClock.uptimeMillis();
		KeyEvent keyEvent = new KeyEvent(eventtime, eventtime, action, keycode,
				0);

		if (!sendMediaKeyEventViaAudioService(keyEvent, c)) {

			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
			c.sendOrderedBroadcast(downIntent, null);
		}

	}

	/**
	 * Send two KeyEvent, one with Action_down and then one with Action_up
	 * 
	 * @param c
	 *            Context
	 * @param keyCode
	 *            See KeyEvent.KEYCODE_
	 */
	private static void sendKey(Context c, int keyCode) {

		sendKey(c, KeyEvent.ACTION_DOWN, keyCode);
		sendKey(c, KeyEvent.ACTION_UP, keyCode);
	}

	public static void sendStopKey(Context c) {
		sendKey(c, KeyEvent.KEYCODE_MEDIA_STOP);
	}

	public static void sendPlayPauseKey(Context c) {
		sendKey(c, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
	}

	/**
	 * Send a AudioManager.ACTION_AUDIO_BECOMING_NOISY event. This event can
	 * only be send by the system and is blocked by SELinux since Android 4.3
	 * 
	 * @param c
	 *            Context
	 */
	@Deprecated
	public static void audioBecomingNoisy(Context c) {
		Intent i = new Intent();
		i.setAction(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		c.sendBroadcast(i);
	}

	public static void changeHeadSetState(Context c, String name,
			Boolean connected) {
		Intent i2 = new Intent();
		i2.setAction(Intent.ACTION_HEADSET_PLUG);
		i2.putExtra("state", (connected ? 1 : 0));
		i2.putExtra("name", name);
		i2.putExtra("microphone", 0);
		c.sendBroadcast(i2);
	}

	/**
	 * Stop the music with the selected method
	 * 
	 * @param c
	 *            Context
	 * @param method
	 *            The selected method (see array.xml)
	 * @return True if the method is known, false otherwise
	 */
	public static boolean stopMusic(Context c, int method) {
		switch (method) {
		default:
			return false;

		case 1: // Stop
			StopHelper.sendStopKey(c);
			break;
		case 2: // Play pause
			StopHelper.sendPlayPauseKey(c);
			break;
		case 3: // plugin and plugout
			StopHelper.headsetPlugAndUnplug(c);
			break;
		case 4: // plugout and plugin
			StopHelper.headsetUnPlugAndPlug(c);
			break;
		case 5: // Getting Noisy
			StopHelper.audioBecomingNoisy(c);
			break;
		case 6: // Mute
			VolumeHelper.setMediaVolume(c, 0);
			break;
		}
		return true;
	}

	/**
	 * Simulation of a handset plug-out and plug-in Useful for songbird
	 */
	public static void headsetUnPlugAndPlug(Context c) {
		StopHelper.changeHeadSetState(c, "Simulated-Headset", false);

		StopHelper.changeHeadSetState(c, "Simulated-Headset", true);
	}

	/**
	 * Simulation of a handset plug-in and plug-out Useful for songbird
	 */
	public static void headsetPlugAndUnplug(Context c) {
		StopHelper.changeHeadSetState(c, "Simulated-Headset", true);

		StopHelper.changeHeadSetState(c, "Simulated-Headset", false);
	}

	/**
	 * Send a key event using audioService.dispatchMediaKeyEvent
	 * Reflection is not used anymore as it didn't work on Lollipop (probably due to ART)
	 * See: http://stackoverflow.com/questions/12573442/
	 * is-google-play-music-hogging-all-action-media-button-intents
	 * 
	 * @param keyEvent
	 * @param method
	 *            dispatchMediaKeyEvent or dispatchMediaKeyEventUnderWakelock
	 * @return true on success, false otherwise
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static boolean sendMediaKeyEventViaAudioService(KeyEvent keyEvent,
			Context c) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AudioManager audioManager = (AudioManager) c
					.getSystemService(Context.AUDIO_SERVICE);
				audioManager.dispatchMediaKeyEvent(keyEvent);
			return true;
		}
		return false;
	}
}
