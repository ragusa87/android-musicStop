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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.bettervectordrawable.VectorDrawableCompat;
import com.blackcrowsteam.musicstop.MainActivity;
import com.blackcrowsteam.musicstop.R;

/**
 * Helper to show, update and hide a notification
 */
public class NotificationHelper {
    private static int currentNotificationId = 0;
    private final int id;
    private final long time;

    public NotificationHelper() {
        id = ++currentNotificationId;
        time = System.currentTimeMillis();
    }

    // Get a reference to the NotificationManager:
    private static NotificationManager getManager(Context c) {
        return (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Show notification
     *
     * @param s       Service
     * @param title   title
     * @param message message
     */
    public void setMessage(Service s, CharSequence title, CharSequence message) {

        Context context = s.getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
         icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);

        }
        final int drawable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_launcher : R.mipmap.ic_launcher_compat;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title).setContentText(message)
                .setContentIntent(contentIntent)
                .setSmallIcon(drawable).setOngoing(true)
                .setWhen(time);
                if(icon != null){
                  //  notificationBuilder.setLargeIcon(icon);
                }
        Notification notification = notificationBuilder.build();

        // Pass the Notification to the NotificationManager:
        getManager(context).notify(id, notification);
        s.startForeground(id, notification);

    }

    /**
     * Remove notification
     *
     * @param context Application context
     */
    public void cancel(Context context) {
        getManager(context).cancel(id);
    }

}