package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.RFUtils;

/**
 * Created by JoinUs4 on 09-12-2015.
 */
public class RFViewPager extends ViewPager {

    private final String INNER = "inner";
    private final String OUTER = "outer";
    private RFFormModel mForm;

    private boolean mIsEnabledSwipe = true;
    private String mFormType;

    public RFViewPager(Context context, RFFormModel form) {
        super(context);
        mForm = form;
        init();
    }

    public RFViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsEnabledSwipe) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mIsEnabledSwipe) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * For enabling and disableing the swipe.
     *
     * @param enabled
     */
    public void setEnabledSwipe(boolean enabled) {
        mIsEnabledSwipe = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int currentPageNumber = getCurrentItem();
        String currentPageTag = mForm.getPages().get(currentPageNumber).getId();
        View view = findViewWithTag(currentPageTag);

        // Here we are calculating whether the current view pager belongs to normal form or form which contains inner form.
        if (mFormType == null) {
            View currentView = view;
            while (currentView != null && currentView.getParent() != null) {

                if (RFUtils.isInstance(View.class, currentView.getParent())) {
                    currentView = (View) currentView.getParent();
                    if (RFUtils.isInstance(RecyclerView.class, currentView)) {
                        mFormType = INNER;
                        break;
                    }
                } else {
                    break;
                }
            }

            if (currentView == null || currentView.getParent() == null) {
                mFormType = OUTER;
            }
        }

        // Height is calculating only for the inner form.
        if (mFormType == null || mFormType.equals(OUTER)) {
            return;
        }

        if (view != null) {
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(getMeasuredWidth() + view.getPaddingRight(), measureHeight(heightMeasureSpec, view) + view.getPaddingLeft());
    }

    private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // set the height from the base view if available
            if (view != null) {
                result = view.getMeasuredHeight();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }
}
