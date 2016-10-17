package ru.mail.park.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.content.ContentValues.TAG;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            measureForUnspecifiedWidth(heightMeasureSpec);
            return;
        }
        boolean heightUnspecified = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED;
        int heightChildrenMode = heightUnspecified ? MeasureSpec.UNSPECIFIED : MeasureSpec.AT_MOST;
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        int lineHeight = 0;
        int sumHeight = 0;

        boolean exactlyWidth = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY;
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthLeft = maxWidth;

        for (int i = 0; i < getChildCount(); i++) {
            View currentView = getChildAt(i);
            if (currentView.getVisibility() == GONE) {
                continue;
            }

            if (currentView.getLayoutParams().width == LayoutParams.MATCH_PARENT) {
                this.measureChild(currentView, MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(maxHeight, heightChildrenMode));
            } else {
                this.measureChild(currentView, MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(maxHeight, heightChildrenMode));
            }

            int childWidth = currentView.getMeasuredWidth();
            //Log.d(TAG, " "+currentView.getLayoutParams().width+" "+currentView.getLayoutParams().height);
            //Log.d(TAG, "width: "+widthLeft+"childW: "+childWidth);
            widthLeft -= childWidth;
            //Log.d(TAG, "NEW width: "+widthLeft+"childW: "+childWidth);
            if (widthLeft >= 0) {
                lineHeight = Math.max(lineHeight, currentView.getMeasuredHeight());
            }
            if (widthLeft <= 0) {
                //check if it was too little space for view
                if (currentView.getLayoutParams().width == LayoutParams.MATCH_PARENT) {
                    this.measureChild(currentView, MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY),
                            Math.max(0, maxHeight - lineHeight));
                } else {
                    this.measureChild(currentView, MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST),
                            Math.max(0, maxHeight - lineHeight));
                }
                if (currentView.getMeasuredWidth() != childWidth) {
                    i--;
                }
                //new line
                exactlyWidth = true;
                widthLeft = maxWidth;
                maxHeight -= lineHeight;
                sumHeight += lineHeight;
                lineHeight = 0;
                if (maxHeight < 0) {
                    maxHeight = 0;
                }
            }
        }

        if (widthLeft > 0) {
            sumHeight += lineHeight;
        }

        int measuredWidth = exactlyWidth ? maxWidth : (maxWidth - widthLeft);
        int measuredHeight =  MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                ? MeasureSpec.getSize(heightMeasureSpec)
                : sumHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void measureForUnspecifiedWidth(int heightMeasureSpec) {
        int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int currentLeft = 0;
        int currentTop = 0;
        int lineHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.layout(
                    currentLeft,
                    currentTop,
                    currentLeft + child.getMeasuredWidth(),
                    currentTop + child.getMeasuredHeight()
            );

            currentLeft = child.getRight();
            if (currentLeft >= getWidth()) {
                if (currentLeft > getWidth()) {
                    i--;    //step back
                } else {
                    lineHeight = Math.max(lineHeight, child.getHeight());
                }
                currentLeft = 0;
                currentTop += lineHeight;
                lineHeight = 0;
            } else {
                lineHeight = Math.max(lineHeight, child.getHeight());
            }
        }
    }

}
