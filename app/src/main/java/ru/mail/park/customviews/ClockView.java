package ru.mail.park.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClockView extends View {

    private static final long MILLIS_IN_12_HOURS = TimeUnit.HOURS.toMillis(12);
    private static final long MILLIS_IN_HOUR = TimeUnit.HOURS.toMillis(1);
    private static final long MILLIS_IN_MINUTE = TimeUnit.MINUTES.toMillis(1);

    private final Paint paint = new Paint();

    private static class Arrow {

        final float radius;
        final float width;
        final long period;

        Arrow(float radius, float width, long period) {
            this.radius = radius;
            this.width = width;
            this.period = period;
        }
    }

    private class State {

        float centerX;
        float centerY;
        long currentTime;

        public void update() {
            centerX = getWidth() / 2f;
            centerY = getHeight() / 2f;
            currentTime = System.currentTimeMillis();
        }

    }

    private final State state = new State();

    private final Arrow hour;
    private final Arrow minute;
    private final Arrow second;

    private int color;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, 0);

        float radius;
        float width;

        radius = typedArray.getDimension(R.styleable.ClockView_hour_radius, 0);
        width = typedArray.getDimension(R.styleable.ClockView_hour_width, 0);

        hour = new Arrow(radius, width, MILLIS_IN_12_HOURS);

        radius = typedArray.getDimension(R.styleable.ClockView_minute_radius, 0);
        width = typedArray.getDimension(R.styleable.ClockView_minute_width, 0);

        minute = new Arrow(radius, width, MILLIS_IN_HOUR);

        radius = typedArray.getDimension(R.styleable.ClockView_second_radius, 0);
        width = typedArray.getDimension(R.styleable.ClockView_second_width, 0);

        second = new Arrow(radius, width, MILLIS_IN_MINUTE);

        color = typedArray.getColor(R.styleable.ClockView_arrow_color, 0);

        typedArray.recycle();

        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.EXACTLY) {
            if (hMode == MeasureSpec.EXACTLY) {
                setMeasuredDimension(width, height);
            } else if (hMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(width, Math.min(width, height));
            } else {
                setMeasuredDimension(width, width);
            }
        } else if (wMode == MeasureSpec.AT_MOST) {
            if (hMode == MeasureSpec.EXACTLY) {
                setMeasuredDimension(Math.min(width, height), height);
            } else if (hMode == MeasureSpec.AT_MOST) {
                int minSize = Math.min(width, height);
                setMeasuredDimension(minSize, minSize);
            } else {
                setMeasuredDimension(width, width);
            }
        } else {
            if (hMode == MeasureSpec.UNSPECIFIED) {
                setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
            } else {
                setMeasuredDimension(height, height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        state.update();

        drawClockArrow(canvas, hour);
        drawClockArrow(canvas, minute);
        drawClockArrow(canvas, second);

        postInvalidateDelayed(100);
    }

    private void drawClockArrow(Canvas canvas, Arrow arrow) {
        long currentTime = state.currentTime;
        float centerX = state.centerX;
        float centerY = state.centerY;

        long period = arrow.period;
        float radius = arrow.radius;
        float strokeWidth = arrow.width;

        double radians = 2 * Math.PI * (currentTime % period) / period - Math.PI / 2;

        float x = (float) (Math.cos(radians) * radius);
        float y = (float) (Math.sin(radians) * radius);

        paint.setStrokeWidth(strokeWidth);
        canvas.drawLine(centerX, centerY, centerX + x, centerY + y, paint);
    }
}
