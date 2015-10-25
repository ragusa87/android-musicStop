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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.blackcrowsteam.musicstop.helpers.TimeHelper;

/**
 * TextView with custom font able to display duration
 */
public class ClockTextView extends TextView {
    private static final String CUSTOM_FONT = "fonts/DroidSansMono.ttf";
    private static final String ATTR_USE_CLOCK_TYPEFACE = "useClockTypeface";
    private final static Typeface sStandardTypeface = Typeface.DEFAULT;
    private static Typeface sClockTypeface;
    private Context mContext;

    public ClockTextView(Context context) {
        super(context);
    }

    public ClockTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClockTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ClockTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (isInEditMode()) {
            return;
        }

        boolean useClockTypeface = attrs.getAttributeBooleanValue(null, ATTR_USE_CLOCK_TYPEFACE, true)
                && !isInEditMode();

        if (sClockTypeface == null) {
            sClockTypeface = Typeface.createFromAsset(mContext.getAssets(), CUSTOM_FONT);
        }

        Paint paint = getPaint();
        paint.setTypeface(useClockTypeface ? sClockTypeface : sStandardTypeface);

    }

    public void setDuration(long duration) {
        setText(TimeHelper.durationToString(mContext, duration));
        postInvalidate();
    }
}
