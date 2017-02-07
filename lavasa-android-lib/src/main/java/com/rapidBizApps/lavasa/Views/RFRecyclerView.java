package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by cdara on 05-05-2016.
 */
public class RFRecyclerView extends RecyclerView {

    private LinearLayout.LayoutParams mLayoutParams;

    public RFRecyclerView(Context context) {
        super(context);
        mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        init();
    }

    public RFRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
}
