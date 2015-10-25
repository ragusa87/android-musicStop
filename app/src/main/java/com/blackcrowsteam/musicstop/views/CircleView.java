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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.blackcrowsteam.musicstop.R;
import com.blackcrowsteam.musicstop.Timer;

public class CircleView extends View {
    private static final int SIGN = 1;
    private static final float mStrokeSize = 20;
    private static final float mMarkerStrokeSize = mStrokeSize;
    private static final float mDotRadius = 25;
    private final Paint mPaint = new Paint();
    private final RectF mArcRect = new RectF();
    private final Paint mFill = new Paint();
    private Timer timer = null;
    private float mRadiusOffset;
    private int mCircleColor = Color.BLACK;
    private int mAccentColor = Color.RED;
    private float mScreenDensity;

    public CircleView(Context context) {
        super(context);
        init(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * Max value (you should pass at least 2 arguments.)
     */
    private static float max(float... sizes) {
        float max = sizes[0];
        for (int i = 1; i < sizes.length; i++) {
            max = Math.max(max, sizes[i]);
        }
        return max;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
        invalidate();
    }

    /**
     * @param context Application context to access resources
     */
    private void init(Context context) {
        Resources resources = context.getResources();
        mScreenDensity = resources.getDisplayMetrics().density;

        if (Build.VERSION.SDK_INT >= 23) {
            mCircleColor = resources.getColor(R.color.circle, context.getTheme());
            mAccentColor = resources.getColor(R.color.hightlight, context.getTheme());
        } else {
            mCircleColor = resources.getColor(R.color.circle);
            mAccentColor = resources.getColor(R.color.hightlight);
        }

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(mCircleColor);
        mFill.setAntiAlias(true);
        mFill.setStyle(Paint.Style.FILL);
        mFill.setColor(mAccentColor);
        float dotDiameter = resources.getDimension(R.dimen.circle_timer_dot_size);
        mRadiusOffset = max(mStrokeSize, dotDiameter, mMarkerStrokeSize, mDotRadius * 2);

    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int xCenter = getWidth() / 2 + 1;
        int yCenter = getHeight() / 2;

        float radius = Math.min(xCenter, yCenter) - mRadiusOffset;
        // just draw a complete circle, no red arc needed
        mPaint.setColor(mCircleColor);

        if (timer == null || !timer.isRunning()) {
            if (timer != null && timer.isStarted()) {
                mPaint.setColor(mAccentColor);
            }
            canvas.drawCircle(xCenter, yCenter, radius, mPaint);
            return;
        }
        //draw a combination of red and white arcs to create a circle
        mArcRect.top = yCenter - radius;
        mArcRect.bottom = yCenter + radius;
        mArcRect.left = xCenter - radius;
        mArcRect.right = xCenter + radius;
        // prevent timer from doing more than one full circle
        final float redPercent = timer.getPercent();
        final float whitePercent = 1 - redPercent;
        // draw white arc here
        mPaint.setColor(mCircleColor);

        canvas.drawArc(mArcRect, 270, SIGN * redPercent * 360, false, mPaint);


        // draw red arc here
        mPaint.setStrokeWidth(mStrokeSize);
        mPaint.setColor(mAccentColor);
        if (SIGN < 0) {
            canvas.drawArc(mArcRect, 270, +whitePercent * 360, false, mPaint);
        } else {
            canvas.drawArc(mArcRect, 270 + (1 - whitePercent) * 360,
                    whitePercent * 360, false, mPaint);
        }

        mPaint.setStrokeWidth(mMarkerStrokeSize);
        float angle = timer.getPercent() * 360;
        // draw 2dips thick marker
        // the formula to draw the marker 1 unit thick is:
        // 180 / (radius * Math.PI)
        // after that we have to scale it by the screen density
        canvas.drawArc(mArcRect, 270 + angle, mScreenDensity *
                (float) (360 / (radius * Math.PI)), false, mPaint);

        drawRedDot(canvas, redPercent, xCenter, yCenter, radius);

        invalidate();


    }

    private void drawRedDot(
            Canvas canvas, float degrees, int xCenter, int yCenter, float radius) {
        mPaint.setColor(mAccentColor);
        float dotPercent;
        dotPercent = 270 + SIGN * degrees * 360;

        final double dotRadians = Math.toRadians(dotPercent);
        canvas.drawCircle(xCenter + (float) (radius * Math.cos(dotRadians)),
                yCenter + (float) (radius * Math.sin(dotRadians)), mDotRadius, mFill);
    }


}
