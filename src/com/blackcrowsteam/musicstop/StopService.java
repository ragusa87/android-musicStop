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

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Countdown service used to stop the music
 * <ul>
 * <li>- The duration is set onStartCommand</li>
 * <li>- The countdown is shown on the notification bar</li>
 * <li>- When the countdown is over, we broadcast an event to the #StopActivity</li>
 * </ul>
 * 
 * @author Constantin Laurent
 * 
 */
public class StopService extends Service {
	// Overwritten onCreate by the value present on strings.xml
	private static int DEFAULT_DURATION = 20;
	// Overwritten onStartCommand by the Intent's extra integer
	private int duration = DEFAULT_DURATION;
	// Overwritten on each timer's tick
	private int remaining = 0;
	// String used for notifications.
	// Use %duration for the duration time
	// Use %remaining for the remaining time
	// Overwritten by strings.xml
	private static String NOTIF_TITLE = "MusicStop";
	private static String NOTIF_START = "Starting " + NOTIF_TITLE
			+ " for %duration";
	private static String NOTIFY_START_TEST = "Immediate test";
	private static String NOTIF_PROGRESS = "%remaining remaning";
	// Timer for the countdown (called every 1s)
	private static Timer timer = null;
	// Count the number of timer's call, so we know when the time is up.
	private int tickCount = 0;

	/**
	 * Replace %duration and %remaining with an human readable countdown
	 * 
	 * @param s
	 *            The original string
	 * @return The new string
	 */
	private String format(String s) {
		return s.replace("%duration", TimeConverter.time(duration)).replace(
				"%remaining", TimeConverter.time(remaining));
	}

	/**
	 * Useless (No need to use any IPC)
	 */
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Load values from strings.xml
	 */
	public void onCreate() {
		super.onCreate();
		DEFAULT_DURATION = Integer.valueOf(getString(R.string.kill_time));

		NOTIF_TITLE = getString(R.string.notify_title);
		NOTIF_PROGRESS = getString(R.string.notify_progress);
		NOTIF_START = getString(R.string.notify_start);
		NOTIFY_START_TEST = getString(R.string.notify_test);
		TimeConverter.loadString(getResources());

		timer = new Timer();
	}

	/**
	 * Start the countdown with the duration present inside the intent
	 * 
	 * @param Intent
	 *            used to start the service
	 * @param flags
	 *            Unused
	 * @param startId
	 *            Unused
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// Useless, but just to be sure!
		if (intent == null) {
			Debug.Log.e(this.getClass().getSimpleName() + " Intent is null");
			timer.cancel();
			return Service.START_NOT_STICKY;
		}
		// Get the duration
		duration = intent.getIntExtra("duration", DEFAULT_DURATION);
		// Start the countdown
		startService();

		// See android.app.Service#onStartCommand
		return Service.START_STICKY;
	}

	/**
	 * When the service stop, we hide the notification and cancel the timer
	 * (just in case)
	 */
	public void onDestroy() {
		timer.cancel();

		// Hide notification
		NotificationHelper.hide(getApplicationContext());

		super.onDestroy();

	}

	/**
	 * Start the service
	 */
	private void startService() {
		// We started a new Countdown
		tickCount = 0;
		remaining = duration;

		String notif_start = format(NOTIF_START);
		if (duration == 0)
			notif_start = format(NOTIFY_START_TEST);

		Debug.Log.v(notif_start);

		// Tick every seconds.
		// When we ticked 'duration' times, we notify the stopActivity
		timer.schedule(new mainTask(), 0, 1000);

		// First notification
		NotificationHelper.setMessage(this, notif_start, format(NOTIF_TITLE),
				format(NOTIF_PROGRESS));

	}

	/**
	 * Timer tick every seconds. When we ticked 'tickCount' times, time's up
	 */
	private class mainTask extends TimerTask {
		public void run() {
			// Time's UP
			if (tickCount >= duration) {
				Debug.Log.v("Timer Stop");

				terminate();
			} else {
				// Number of remaining seconds before time's up
				remaining = duration - tickCount;
				Debug.Log.v("Tick " + tickCount + " of " + duration + " => "
						+ remaining + " ticks remaining");

				// Notify
				NotificationHelper.setMessage(StopService.this,
						format(NOTIF_START), format(NOTIF_TITLE),
						format(NOTIF_PROGRESS));

				// Tick
				tickCount++;

			}

		}
	}

	/**
	 * The countdown is over. - Stop the music - Notify the view - stop the
	 * service Then the countdown service stop
	 */
	private void terminate() {
		Debug.Log.v("StopSelf");
		try {
			int method = PrefHelper.getPrefMethod(getApplicationContext());
			if (!StopHelper.stopMusic(getApplicationContext(), method)) {
				Debug.Log.e("Unknow stop method");
			}

		} catch (Exception e) {
			Debug.Log.e("Cant stop MUSIC !", e);
		}

		try {
			Intent i = new Intent(StopActivity.BROADCAST_STOP_ACTION);
			sendBroadcast(i);

		} catch (Exception e) {
			Debug.Log.e("Cant send STOP event", e);
		}

		// Stop the service
		stopSelf();
	}
}
