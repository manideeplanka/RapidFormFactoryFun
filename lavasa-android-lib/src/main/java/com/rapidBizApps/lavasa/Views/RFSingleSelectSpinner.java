package com.rapidBizApps.lavasa.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rapidBizApps.lavasa.Listeners.RFSpinnerListener;

import java.util.List;

/**
 * Custom spinner to allow single select in a spinner
 *
 * @author tbaladari
 */
public class RFSingleSelectSpinner extends Spinner implements
        DialogInterface.OnClickListener {
    public static AlertDialog.Builder mBuilder;
    public ArrayAdapter<String> mSpinnerAdapter;
    int[] mSelection = null;
    int mSelectedPosition = -1, mPrevSelectedPosition = -1;
    private String[] mItems = null;
    private String mSelectedString = "", mFirstSelectedString = "";
    private Context mContext;
    private RFSpinnerListener mSpinnerListener;
    // This variable is for changing the text color of the selected string of
    // the spinner
    private boolean mChangeSelectedTextColor = false;

    private AlertDialog mDialog;

    public RFSingleSelectSpinner(Context context, RFSpinnerListener spinnerListener) {
        super(context);
        this.mSpinnerListener = spinnerListener;
        mContext = context;
        mSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(mSpinnerAdapter);
    }


    /**
     * Constructor used by the layout inflater.selected * @param context
     *
     * @param attrs
     */
    public RFSingleSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        super.setAdapter(mSpinnerAdapter);

    }


    public void setItem(String[] items) {
        mItems = items;
        mSelection = new int[mItems.length];

    }

    public void setItem(List<String> items) {

        mItems = new String[items.size()];

        for (int i = 0; i < items.size(); i++) {

            mItems[i] = items.get(i);

        }

    }

    @Override
    public boolean performClick() {
        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(
                    mContext, android.R.style.Theme_Dialog));
            mBuilder.setTitle(
                    "Please Select")
                    .setSingleChoiceItems(mItems, mSelectedPosition,
                            this);


            mBuilder.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {

                    mSpinnerAdapter.add(firstBuildItemString());
                    mSelectedPosition = mPrevSelectedPosition;
                    mSelectedString = mFirstSelectedString;

                    mBuilder = null;
                }
            });

            mDialog = mBuilder.create();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }

        }
        return true;
    }

    @Override
    public void setSelection(int pos) {
        mSelectedPosition = pos;
        mPrevSelectedPosition = pos;
        mSelectedString = mItems[pos];
        mFirstSelectedString = mItems[pos];

    }

    public void setClickSelection(int pos) {

        mSelectedPosition = pos;
        mSelectedString = mItems[pos];

    }

    public String buildItemString() {

        return mSelectedString;
    }

    public String firstBuildItemString() {

        return mFirstSelectedString;
    }

    public String getmSelectedString() {

        return mSelectedString;
    }

    public int getSelectedID() {

        return mSelectedPosition;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        setClickSelection(which);

        mSelectedPosition = which;
        // mSpinnerAdapter.clear();

        mSpinnerAdapter.add(buildItemString());
        mFirstSelectedString = mSelectedString;
        mPrevSelectedPosition = mSelectedPosition;
        mSpinnerListener.onDoneClick(mSelectedPosition);

        dialog.dismiss();
        mBuilder = null;
    }

}
