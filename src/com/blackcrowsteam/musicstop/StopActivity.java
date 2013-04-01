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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity used to interact with the stopMusic service. Working in 3 steps :
 * <ul>
 * <li>Countdown input with a NumberPicker element</li>
 * <li>Starting the countdown service for X seconds</li>
 * <li>Get notified when the countdown is over</li>
 * </ul>
 * 
 * @author Constantin Laurent
 */
public class StopActivity extends Activity {
	/**
	 * Action broadcasted when the countdown is over
	 */
	public final static String BROADCAST_STOP_ACTION = "com.blackcrowsteam.musicstop.STOP";

	/**
	 * The maximum value who can be used for the countdown 23:59:59 hours in
	 * seconds
	 */
	private final static int MAX_TIMER = 86399;

	/**
	 * NumberPicker's Inputs
	 * */
	private NumberPicker pickerHours;
	private NumberPicker pickerMin;

	/**
	 * Strings overwritten by strings.xml
	 */
	private static String BUTTON_GO_TEXT = "Run";
	private static String BUTTON_CANCEL_TEXT = "Cancel";

	/**
	 * Listener : Used to know when the countdown is over
	 */
	private final BroadcastReceiver bcr = new BroadcastReceiver() {
		/**
		 * Called by stopService by broadcasting BROADCAST_STOP_ACTION.
		 * 
		 * @see #stopMusic
		 * @Override
		 */
		public void onReceive(Context context, Intent intent) {
			Debug.Log.v(intent.getAction());
			// Update the button label
			switchGoButton(false);
		}
	};

	/**
	 * Linking the view with the activity
	 * 
	 * @Override
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		BUTTON_CANCEL_TEXT = getString(android.R.string.cancel);
		BUTTON_GO_TEXT = getString(R.string.btn_go_txt);

		pickerHours = (NumberPicker) findViewById(R.id.pickerHours);
		pickerMin = (NumberPicker) findViewById(R.id.pickerMinutes);

		pickerHours.setMinValue(0);
		pickerHours.setMaxValue(24);
		pickerHours.setFocusable(true);
		pickerHours.setFocusableInTouchMode(true);

		pickerMin.setMinValue(0);
		pickerMin.setMaxValue(59);
		pickerMin.setFocusable(true);
		pickerMin.setFocusableInTouchMode(true);

		switchGoButton();
	}

	/**
	 * Listen to the countdown when the activity is resumed
	 * 
	 * @Override
	 */
	public void onResume() {
		super.onResume();
		registerReceiver(bcr, new IntentFilter(BROADCAST_STOP_ACTION));
		loadPickers();

		// Be sure the Go-button's label is right !
		switchGoButton();

	}

	/**
	 * Stop listening to the countdown when the activity is paused
	 * 
	 * @Override
	 */
	public void onPause() {
		super.onPause();
		unregisterReceiver(bcr);
		// Save the timer
		PrefHelper.setTimer(getApplicationContext(), getDurationCountdown()
				+ "");

	}

	/**
	 * When a button is pressed, we choose the action to execute (For now, there
	 * is only one button)
	 * 
	 * @param v
	 *            The pressed button
	 */
	public void btnManager(View v) {
		switch (v.getId()) {
		case R.id.btnGo:
			v.requestFocus();

			// START the countdown (or stop it if it's already running)
			if (isCountdownRunning()) {
				stopCountdown();
			} else {
				startCountdown();
			}
			break;
		default:
			Debug.Log.e("NOT IMPLEMENTED");
			break;
		}

	}

	/**
	 * Load the duration countdown from preferences. Put the result into the
	 * pickers (hours, minutes, seconds)
	 * 
	 * Use the value in strings.xml in case of problems
	 */
	private void loadPickers() {

		String timer = "";
		int time = 0;
		try {
			// Load the duration from settings
			timer = PrefHelper.getTimer(getApplicationContext());
			time = Integer.valueOf(timer);

		} catch (NumberFormatException f) {
			// Toast
			Toast.makeText(getApplicationContext(),
					getString(R.string.error_bad_kill_time), Toast.LENGTH_LONG)
					.show();
			Debug.Log.e("Unable to convert " + timer + " to int", f);

			// Use the value in strings.xml
			try {
				time = Integer.valueOf(getString(R.string.kill_time));
			} catch (NumberFormatException f2) {
			}

		} catch (Exception e) {
			Debug.Log.e("LoadPickers exception", e);
		}

		// Be sure the max-value can be managed by the pickers
		if (time > MAX_TIMER)
			time = MAX_TIMER;

		pickerHours.setValue(TimeConverter.getNumberOfHours(time));
		pickerMin.setValue(TimeConverter.getNumberOfMinutes(time));
	}

	/**
	 * Get the duration countdown from the pickers
	 * 
	 * @return the duration in seconds
	 */
	private int getDurationCountdown() {
		int h = pickerHours.getValue();
		int m = pickerMin.getValue();
		return h * 3600 + m * 60;
	}

	/**
	 * Auto-detect the button's functionality
	 * 
	 * @see #switchGoButton(boolean)
	 */
	private void switchGoButton() {
		switchGoButton(isCountdownRunning());
	}

	/**
	 * This method change the GO button's label The button has 2 functions:
	 * <ul>
	 * <li>Run MusicStop (if service is stopped)</li>
	 * <li>Cancel MusicStop (if service is running)</li>
	 * </ul>
	 * 
	 * @param allowCancel
	 *            Indicates whether the service is running
	 */
	private void switchGoButton(boolean allowCancel) {
		Button b = (Button) findViewById(R.id.btnGo);
		b.setText((allowCancel ? BUTTON_CANCEL_TEXT : BUTTON_GO_TEXT));
	}

	/**
	 * Get the countdown input and start the countdown service
	 * 
	 * @see #startCountdown(int)
	 */
	private void startCountdown() {
		// Default countdown's value from strings.xml
		int kill_time = Integer.valueOf(getString(R.string.kill_time));
		// Parse the user's value
		try {
			kill_time = getDurationCountdown();

		} catch (Exception e) {
			Debug.Log.e("BAD KILL TIME", e);
			Toast.makeText(getApplicationContext(),
					getString(R.string.error_bad_kill_time), Toast.LENGTH_LONG).show();
		}
		// launch
		startCountdown(kill_time);
	}

	/**
	 * Start the countdown's service for a specified time of seconds (starts to
	 * count rebourd)
	 * 
	 * @param kill_time
	 *            Number of seconds for the countdown
	 */
	private void startCountdown(int kill_time) {
		// Debug
		Debug.Log.v("Starting service! (" + kill_time + ")");

		// Start the countdown service with the specified duration
		Intent i = new Intent(getApplicationContext(), StopService.class);
		i.putExtra("duration", kill_time);
		startService(i);

		switchGoButton();
	}

	/**
	 * Cancel the countdown The service will stop, but not the music !
	 */
	private void stopCountdown() {
		Toast.makeText(getApplicationContext(),
				getString(R.string.action_cancel), Toast.LENGTH_LONG).show();
		stopService(new Intent(getApplicationContext(), StopService.class));
		switchGoButton();
	}

	/**
	 * Indicate if the countdown's service is running
	 * 
	 * @return true if it is, false otherwise
	 */
	private boolean isCountdownRunning() {

		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {

			if (StopService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * STOP MUSIC Restart the countdown's service for 0 seconds, so the music
	 * stops
	 */
	private void stopMusicNow() {
		startCountdown(0);
	}

	/**
	 * Choose the action to start from the menu
	 * <ul>
	 * <li>Stop music in 0s</li>
	 * <li>Open the preference activity</li>
	 * </ul>
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Open the Preferences activity
		switch (item.getItemId()) {
		case R.id.menuitem_test:
			stopMusicNow();
			return true;
		case R.id.menuitem_pref:
			startActivity(new Intent(this.getApplicationContext(),
					Preferences.class));
			return true;
		case R.id.menuitem_about:
			/*PackageInfo pInfo;
			String version = getString(R.string.app_name);
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				version += " " + pInfo.versionName + "<br>";
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getString(R.string.menu_about_label));
			TextView textView = new TextView(this);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			textView.setText(Html.fromHtml(getResources().getString(
					R.string.about).replace("%ver", version)));
			dialogBuilder.setView(textView);
			dialogBuilder.setPositiveButton(getString(android.R.string.ok),
					null).show();
					*/
			AboutHelper.showAbout(this);
			return true;
		}
		return false;
	}

	/**
	 * Create the menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Build menu from XML
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
}
