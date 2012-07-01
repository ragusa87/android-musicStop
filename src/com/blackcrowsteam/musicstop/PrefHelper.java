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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class is a helper for getting and setting android's preferences
 * 
 * @author Laurent Constantin
 * 
 */
public class PrefHelper {
	private static int DEFAULT_STOP_METHOD = 1;

	/**
	 * Get the selected method to stop the music Values go from 1 to 4,
	 * according to the array.xml file
	 * 
	 * @param c
	 *            Activity's context
	 * @return An integer according to array.xml
	 */
	public static int getPrefMethod(Context c) {
		// Choose the correct method from settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		String s = sharedPrefs.getString("prefMethods", DEFAULT_STOP_METHOD
				+ "");
		int method = DEFAULT_STOP_METHOD;
		try {
			method = Integer.valueOf(s.trim());
		} catch (NumberFormatException e) {
			Debug.Log.e("BAD METHOD (from prefs)", e);
			method = -1;
		}
		return method;
	}

	/**
	 * Save the timer duration
	 * 
	 * @param c
	 *            Activity's context
	 * @param timer
	 *            The countdown duration in seconds (String)
	 */
	public static void setTimer(Context c, String timer) {
		// Save the duration into settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		SharedPreferences.Editor ed = sharedPrefs.edit();
		ed.putString("prefDuration", timer);
		Debug.Log.v("Saving : " + timer);
		ed.commit();
	}

	/**
	 * Get the countdown duration from preferences
	 * 
	 * @param c
	 *            Activity's Context
	 * @return The countdown duration in string
	 */
	public static String getTimer(Context c) {
		// Load the duration from settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		String timer = sharedPrefs.getString("prefDuration",
				c.getString(R.string.kill_time));
		Debug.Log.v("Loading " + timer);

		return timer;
	}
	
	public static boolean getFadeIn(Context c){
		// Load the checkbox from settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		Boolean fadein = sharedPrefs.getBoolean("prefFadein", true);
		Debug.Log.v("Loading pref fade-in: " + fadein);
		return fadein;	
	}
}
