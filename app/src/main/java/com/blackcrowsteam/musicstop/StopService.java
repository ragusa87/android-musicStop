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

package com.blackcrowsteam.musicstop;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.blackcrowsteam.musicstop.helpers.Debug;
import com.blackcrowsteam.musicstop.helpers.NotificationHelper;
import com.blackcrowsteam.musicstop.helpers.StopHelper;
import com.blackcrowsteam.musicstop.helpers.TimeHelper;
import com.blackcrowsteam.musicstop.helpers.VolumeHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Countdown service used to stop the music
 * <ul>
 * <li>- The duration is set onStartCommand</li>
 * <li>- The countdown is shown on the notification bar</li>
 * <li>- When the countdown is over, we broadcast an event to the #StopActivity</li>
 * </ul>
 *
 * @author Constantin Laurent
 */
public class StopService extends Service {
    public static final String BROADCAST_STOP_ACTION = "com.blackcrowsteam.musicstop.STOP";
    public static final String BROADCAST_TICK_ACTION = "com.blackcrowsteam.musicstop.TICK";
    public static final String EXTRA_TIMER = "com.blacrowsteam.musicstop.timer";

    // Timer for the countdown (called every 1s)
    private static Timer mJavaTimer = new Timer();
    // Countdown Object (setting)
    private com.blackcrowsteam.musicstop.Timer mTimer = null;
    // WaveLock (acquired on startService, released onDestroy).
    // Used so the countdown is not canceled when the phone is locked.
    private PowerManager.WakeLock mSleepLock = null;
    private NotificationHelper mNotificationHelper = null;


    /**
     * Replace %duration and %remaining with an human readable countdown
     *
     * @param id The original string res id
     * @return The new string
     */
    private String format(int id) {
        Context context = getBaseContext();
        CharSequence sRemain = TimeHelper.remainingString(mTimer.getStopTime());
        String sDuration = TimeHelper.durationToString(this.getBaseContext(), mTimer.getDuration()).toString();
        String s = context.getString(id);
        return s.replace("%duration", sDuration).replace("%remaining", sRemain);
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
        final PowerManager power = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Debug.Log.v("Lock.Create");
        mSleepLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));

    }


    private void broadcastTimerAction(String action) {
        Intent i = new Intent(action);
        i.putExtra(EXTRA_TIMER, mTimer);
        sendBroadcast(i);
    }

    /**
     * Start the countdown with the duration present inside the intent
     *
     * @param intent  used to start the service
     * @param flags   Unused
     * @param startId Unused
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Get the duration
        Bundle bundle = intent.getExtras();
        this.mTimer = bundle.getParcelable(EXTRA_TIMER);
        if (mTimer == null) {
            throw new IllegalStateException("Service must be started with a timer");
        }


        // Start the countdown
        startService();

        // See android.app.Service#onStartCommand
        return Service.START_STICKY;
    }

    /**
     * When the service stop, we hide the notification and cancel the mJavaTimer
     * (just in case)
     */
    public void onDestroy() {
        mTimer = null;

        mJavaTimer.cancel();
        // Relase the mSleepLock
        if (mSleepLock != null && mSleepLock.isHeld()) {
            Debug.Log.v("Lock.Release");
            mSleepLock.release();
        }

        // Hide notification
        if (mNotificationHelper != null)
            mNotificationHelper.cancel(getApplicationContext());

        super.onDestroy();

    }

    /**
     * Start the service
     */
    private void startService() {
        Log.w("MUSICSTOP", "START " + mTimer);

        // Acquire the mSleepLock
        if (mSleepLock != null && !mSleepLock.isHeld()) {
            Debug.Log.v("Lock.acquire");
            mSleepLock.acquire();
        }
        mNotificationHelper = new NotificationHelper();

        // First notification
        if (mTimer.getDuration() > 0) {
            mNotificationHelper.setMessage(this, getString(R.string.notify_title),
                    format(R.string.notify_progress));
        }

        // Tick every seconds.
        // When we ticked 'duration' times, we notify the stopActivity
        mJavaTimer = new Timer();
        mJavaTimer.schedule(new TickingTask(), 0, 1000);
        mTimer.start();

        broadcastTimerAction(BROADCAST_TICK_ACTION);
    }

    /**
     * The countdown is over. - Stop the music - Notify the view - stop the
     * service Then the countdown service stop
     */
    private void terminate() {

        // Notify stopping
        mNotificationHelper.setMessage(this, getString(R.string.notify_title),
                format(R.string.notify_stopping));

        int volume = VolumeHelper.getMediaVolume(getApplicationContext());
        final boolean mFadeIn = true;
        final StopHelper.Method method = StopHelper.Method.STOP;
        try {

            // Fade-in
            if (mFadeIn) {
                broadcastTimerAction(BROADCAST_TICK_ACTION);
                VolumeHelper.muteMediaVolume(getApplicationContext());
            }

            // STOP MUSIC
            if (!StopHelper.stopMusic(getApplicationContext(), method)) {
                Debug.Log.e("Unknown stop method");
            }

        } catch (Exception e) {
            Debug.Log.e("Cant stop MUSIC !", e);
        }

        // Restore volume
        if (mFadeIn && method != StopHelper.Method.MUTE) {
            VolumeHelper.sleep(1000);
            VolumeHelper.setMediaVolume(getApplicationContext(), volume);
        }

        try {
            broadcastTimerAction(BROADCAST_STOP_ACTION);
        } catch (Exception e) {
            Debug.Log.e("Cant send STOP event", e);
        }

    }

    /**
     * Timer tick every seconds. When we ticked 'mTickCount' times, time's up.
     * I should have used CountDownTimer
     */
    private class TickingTask extends TimerTask {
        public void run() {
            if (mTimer == null || !mTimer.isStarted()) {
                return;
            }

            if (!mTimer.isRunning()) {
                Debug.Log.v("Timer Stop");
                this.cancel();
                broadcastTimerAction(BROADCAST_TICK_ACTION);

                terminate();

                // Stop the service
                stopSelf();

            } else {
                // Notify
                if (mNotificationHelper != null)
                    mNotificationHelper.setMessage(StopService.this, getString(R.string.notify_title),
                            format(R.string.notify_progress));
                broadcastTimerAction(BROADCAST_TICK_ACTION);
            }

        }
    }
}