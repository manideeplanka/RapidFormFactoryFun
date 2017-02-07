package com.rapidBizApps.lavasa.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.rapidBizApps.lavasa.Listeners.RFSpinnerListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A Spinner view that does not dismiss the dialog displayed when the control is
 * "dropped down" and the user presses it. This allows for the selection of more
 * than one option.
 */
public class RFMultiSelectSpinner extends Spinner implements
        OnMultiChoiceClickListener {
    static AlertDialog.Builder mBuilder;
    public final String TAG = RFMultiSelectSpinner.class.getName();
    public ArrayAdapter<String> mProxyAdapter;
    public Context mContext;
    private String[] mItems = null;
    private String[] mItemIDs = null;
    private boolean[] mSelection = null, mFirstSelection = null;
    private RFSpinnerListener mSpinnerListener;

    /**
     * Constructor for use when instantiating directly.
     *
     * @param context
     */
    public RFMultiSelectSpinner(Context context, RFSpinnerListener spinnerListener) {
        super(context);

        this.mSpinnerListener = spinnerListener;

        mContext = context;
        mProxyAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);

    }

    /**
     * Constructor used by the layout inflater.
     *
     * @param context
     * @param attrs
     */
    public RFMultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mProxyAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
            // mSelection[0] = false;
            mProxyAdapter.clear();

            //setSelection(0);
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performClick()

    //TODO: kumar
    {
   /*     long seconds = System.currentTimeMillis();

        if (seconds > ((MainActivity) mContext).prevOnClickSeconds + 1000)
        {
            ((MainActivity) mContext).prevOnClickSeconds = seconds;*/

        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(getContext());

            mBuilder.setTitle("Please Select").setMultiChoiceItems(
                    mItems, mSelection, this);
            mBuilder.setPositiveButton("Done",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(
                                DialogInterface paramDialogInterface,
                                int paramInt) {
                            mSpinnerListener.onDoneClick(getSelectedIndicies());
                            for (int i = 0; i < mItems.length; ++i) {
                                mFirstSelection[i] = mSelection[i];
                            }

                            mProxyAdapter.add(buildSelectedItemString());
                            mBuilder = null;

                        }
                    });

            mBuilder.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {

                    mProxyAdapter.add(initialBuildSelectedItemString());
                    mBuilder = null;
                }
            });

            AlertDialog dialog = mBuilder.create();

            if (!dialog.isShowing()) {
                dialog.show();
            }

        }
        // }
        return true;
    }


    /**
     * MultiSelectSpinner does not support setting an adapter. This will throw
     * an exception.
     *
     * @param adapter
     */
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    /**
     * Sets the options for this spinner.
     *
     * @param items
     */

    public void setItems(String[] items) {
        mItems = items;
        mSelection = new boolean[mItems.length];
        mFirstSelection = new boolean[mItems.length];

        Arrays.fill(mSelection, false);
    }

    /**
     * Sets the options for this spinner.
     *
     * @param items
     */
    public void setItems(List<String> items, List<String> idList) {


        mItems = new String[items.size()];
        mItemIDs = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {

            mItems[i] = items.get(i);
            mItemIDs[i] = idList.get(i);
        }

        // mItems = items.toArray(new String[items.size()]);
        mSelection = new boolean[mItems.length];
        mFirstSelection = new boolean[mItems.length];
        Arrays.fill(mSelection, false);
        Arrays.fill(mFirstSelection, false);

    }

    /**
     * Sets the selected options based on a list of string.
     *
     * @param selection
     */
    public void setSelection(List<String> selection) {

        if (selection != null) {

            for (String sel : selection) {
                for (int j = 0; j < mItems.length; ++j) {
                    if (mItemIDs[j].equals(sel)) {
                        mSelection[j] = true;
                        mFirstSelection[j] = true;

                    }
                }
            }
        }

        // mFirstSelection =mSelection;

    }

    /**
     * Sets the selected options based on an array of positions
     *
     * @param selectedIndicies
     */

    public void setSelection(int[] selectedIndicies) {
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
    }

    /**
     * Returns a list of strings, one for each selected item.
     *
     * @return
     */
    /*
     * public List<String> getSelectedStrings() { List<String> selection = new
     * LinkedList<String>(); for (int i = 0; i < mItems.length; ++i) { if
     * (mSelection[i]) { selection.add(mItems[i]); } } return selection; }
     */

    /**
     * Returns a list of positions, one for each selected item.
     *
     * @return
     */
    public List<String> getSelectedIndicies() {

        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) {
                selection.add(mItemIDs[i]);
            }
        }

        return selection;
    }

    /**
     * Builds the string for display in the spinner.
     *
     * @return comma-separated list of selected items
     */
    public String buildSelectedItemString() {

        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(mItems[i]);
            }
        }

        return sb.toString();
    }

    public String initialBuildSelectedItemString() {

        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < mItems.length; ++i) {
            mSelection[i] = mFirstSelection[i];
            if (mFirstSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(mItems[i]);
            }
        }


        Log.e("", " Array : " + Arrays.toString(mSelection));

        return sb.toString();
    }
}
