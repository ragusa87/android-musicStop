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

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bettervectordrawable.VectorDrawableCompat;
import com.blackcrowsteam.musicstop.helpers.AboutDialogHelper;
import com.blackcrowsteam.musicstop.views.CountDownView;
import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;

public class MainActivity extends AppCompatActivity implements HmsPickerDialogFragment.HmsPickerDialogHandler {
    private static final String DURATION_SAVE_KEY = "LAST_DURATION";
    private CountDownView mCountDown;
    private Timer mTimer = null;
    private final BroadcastReceiver bcr = new BroadcastReceiver() {
        /**
         * Called by stopService by broadcasting BROADCAST_STOP_ACTION.
         */
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(StopService.BROADCAST_STOP_ACTION)) {
                stop();
                return;
            }
            if (intent.getAction().equals(StopService.BROADCAST_TICK_ACTION)) {
                mTimer = intent.getExtras().getParcelable(StopService.EXTRA_TIMER);
                refresh();
                return;
            }
        }
    };
    private long mLastDuration;
    private AboutDialogHelper mAbout;

    private void openPicker() {
        new HmsPickerBuilder().setFragmentManager(getSupportFragmentManager()).setStyleResId(R.style.BetterPickersDialogFragment).show();
    }

    /**
     * Update view
     */
    private void refresh() {
        mCountDown.setTimer(mTimer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VectorDrawableCompat.enableResourceInterceptionFor(getResources(), R.drawable.play, R.drawable.stop, R.drawable.ic_launcher);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAbout = new AboutDialogHelper(this);
        mCountDown = (CountDownView) findViewById(R.id.countdown);
        mCountDown.setOnDurationClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer == null || !mTimer.isRunning()) {
                    openPicker();
                }
            }
        });
        mCountDown.setOnActionButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer != null && mTimer.isRunning()) {
                    stop();
                } else {
                    // When a stop is in progress, button is hidden.
                    start();
                }
            }
        });


        if (savedInstanceState != null) {
            mAbout.load(savedInstanceState);
            mTimer = Timer.fromSavedInstance(savedInstanceState);
        }
    }

    /**
     * Stop the service and create a new timer
     */
    private void stop() {
        stopService(new Intent(getApplicationContext(), StopService.class));
        mTimer = new Timer(mTimer.getDuration());
        refresh();
    }

    /**
     * Start the countdown service
     */
    private void start() {
        if (mTimer == null) {
            throw new RuntimeException("Timer cannot be null");
        }
        Intent i = new Intent(getApplicationContext(), StopService.class);
        i.putExtra(StopService.EXTRA_TIMER, mTimer);
        startService(i);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * When user pick a time, we set the timer duration
     *
     * @param reference refrence
     * @param hours     Hours
     * @param minutes   Minutes
     * @param seconds   Seconds
     * @see HmsPickerDialogFragment.HmsPickerDialogHandler
     */
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        this.mTimer = new Timer((hours * 3600 + minutes * 60 + seconds) * 1000);
        mLastDuration = mTimer.getDuration();
        refresh();
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(StopService.BROADCAST_STOP_ACTION);
        filter.addAction(StopService.BROADCAST_TICK_ACTION);

        registerReceiver(bcr, filter);
        // Load duration
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mLastDuration = prefs.getLong(DURATION_SAVE_KEY, 20 * 60 * 1000);

        if (mTimer == null) {
            mTimer = new Timer(mLastDuration);
        }
        refresh();
        handlePermission();
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(bcr);

        // Save duration
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(DURATION_SAVE_KEY, mLastDuration);
        editor.apply();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        mAbout.save(savedInstanceState);
        mTimer.save(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            mAbout.showAbout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void handlePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)
                == PackageManager.PERMISSION_GRANTED) {
            return;

        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WAKE_LOCK)) {
            // TODO Explain why we need permission
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WAKE_LOCK}, 0
            );
        }
    }
}
