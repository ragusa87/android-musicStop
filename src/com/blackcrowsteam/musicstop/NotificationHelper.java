package com.blackcrowsteam.musicstop;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

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
/**
 * Helper to show, update and hide a notification
 * @todo Refactor, ugly code there..
 */
public class NotificationHelper {
	private static final int NOTIF_ID = 1;

	// Get a reference to the NotificationManager:
	private static NotificationManager getManager(Context c) {
		return (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	// new Notification
	private static Notification notificationFactory(CharSequence startMsg) {
		int icon = R.drawable.ic_launcher;
		
		long when = System.currentTimeMillis();
		return new Notification(icon, startMsg, when);
	}

	/**
	 * Show Notification
	 * @param context Application context
	 * @param startMsg Ticker texte
	 * @param title Notification title
	 * @param message Notification Message
	 */
	public static void setMessage(Service s, CharSequence startMsg, CharSequence title,
			CharSequence message) {
		
		Context context = s.getApplicationContext();
		
		Notification notif = notificationFactory(startMsg);
		notif.flags = Notification.FLAG_ONGOING_EVENT;

		
		Intent notificationIntent = new Intent(context, StopActivity.class);
		// Open the app only once
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notif.setLatestEventInfo(context, title, message,contentIntent );

		// Pass the Notification to the NotificationManager:
		getManager(context).notify(NOTIF_ID, notif);

		
		s.startForeground(NOTIF_ID, notif);
		
	}
	/**
	 * Remove notification
	 * @param context Application context
	 */
	public static void hide(Context context){
		getManager(context).cancel(NOTIF_ID);
	}

}
