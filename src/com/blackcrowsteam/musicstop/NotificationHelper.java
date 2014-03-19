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

import android.support.v4.app.NotificationCompat;
import android.app.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/**
 * Helper to show, update and hide a notification
 * 
 * @todo Refactor, ugly code there..
 */
public class NotificationHelper {
	private static int currentNotifId = 0;
	private final int id;
	private final long time;
	
	public NotificationHelper(){
		id = ++currentNotifId;
		time = System.currentTimeMillis();
	}

	// Get a reference to the NotificationManager:
	private static NotificationManager getManager(Context c) {
		return (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

/**
 * Show notification
 * @param s Service
 * @param title title 
 * @param message message
 */
	public void setMessage(Service s, CharSequence title, CharSequence message) {

		Context context = s.getApplicationContext();

		Intent notificationIntent = new Intent(context, StopActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


		Notification notif = new NotificationCompat.Builder(context)
				.setContentTitle(title).setContentText(message)
				.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.ic_launcher).setOngoing(true)
				.setWhen(time)
				.build();
		
		// Pass the Notification to the NotificationManager:
		getManager(context).notify(id, notif);
		s.startForeground(id, notif);

	}

	/**
	 * Remove notification
	 * 
	 * @param context
	 *            Application context
	 */
	public void cancel(Context context) {
		getManager(context).cancel(id);
	}

}
