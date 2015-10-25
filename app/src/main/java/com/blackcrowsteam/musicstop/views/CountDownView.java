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
package com.blackcrowsteam.musicstop.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.blackcrowsteam.musicstop.R;
import com.blackcrowsteam.musicstop.Timer;

/**
 * View to display the circle, the duration text and the buttons
 */
public class CountDownView extends RelativeLayout {

    private CircleView mCircle;
    private ClockTextView mText;
    private FloatingActionButton mBtn;
    private Drawable mDrawablePlay;
    private Drawable mDrawableStop;


    public CountDownView(Context context) {
        super(context);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        View v = inflate(context, R.layout.countdown, null);
        this.addView(v);
        mText = (ClockTextView) findViewById(R.id.countdown_text);
        mCircle = (CircleView) findViewById(R.id.countdown_circle);
        mBtn = (FloatingActionButton) findViewById(R.id.countdown_fab);
        mDrawablePlay = context.getDrawable(R.drawable.play);
        mDrawableStop = context.getDrawable(R.drawable.stop);
    }

    public void setOnDurationClick(OnClickListener listener) {
        this.mText.setOnClickListener(listener);
    }

    public void setOnActionButtonClick(OnClickListener listener) {
        this.mBtn.setOnClickListener(listener);
    }

    /**
     * @param timer Update view with the specified timer
     * @throws IllegalArgumentException Timer is null
     */
    public void setTimer(Timer timer) {
        if (timer == null) {
            throw new IllegalArgumentException("Timer cannot be null");
        }
        this.mBtn.setVisibility(timer.isStarted() && ! timer.isRunning() ? INVISIBLE : VISIBLE);
        this.mBtn.setImageDrawable(timer.isRunning() ? mDrawableStop : mDrawablePlay);
        this.mBtn.invalidate();
        this.mCircle.setTimer(timer);
        this.mText.setDuration(timer.isRunning() ? timer.getRemainingDuration() : timer.getDuration());
    }
}
