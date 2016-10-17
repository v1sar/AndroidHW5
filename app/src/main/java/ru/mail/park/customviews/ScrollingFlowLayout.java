package ru.mail.park.customviews;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class ScrollingFlowLayout extends FlowLayout {

    private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private boolean isScrolling;
    private float startX;
    private float prevX;

    public ScrollingFlowLayout(Context context) {
        super(context);
    }

    public ScrollingFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isScrolling = false;
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = prevX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE: {
                if (isScrolling) {
                    return true;
                }

                final float xDiff = Math.abs(ev.getX() - startX);

                if (xDiff > touchSlop) {
                    isScrolling = true;
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_MOVE:
                scrollBy((int) (prevX - ev.getX()), 0);
                prevX = ev.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isScrolling = false;
                break;
        }
        return true;
    }
}