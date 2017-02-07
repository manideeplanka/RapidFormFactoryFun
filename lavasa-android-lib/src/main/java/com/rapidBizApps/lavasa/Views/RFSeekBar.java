package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;

/**
 * Created by cdara on 10-05-2016.
 */
public class RFSeekBar extends RFBaseElement {


    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private LinearLayout mSeekBarView;
    private TextView mSeekBarLabel;
    private SeekBar mSeekBarInt;
    private SeekBar mSeekBarFloat;
    private EditText mSeekBarValue;
    private int mSeekBarIntValue = 0;
    private float mSeekBarFloatValue = 0;
    private Context mContext;
    private float mMaxValue = 100;

    public RFSeekBar(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mElementModel = elementModel;
        mListener = getElementListeners();
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSeekBarView = (LinearLayout) inflater.inflate(R.layout.seekbar, null);
        mSeekBarInt = (SeekBar) mSeekBarView.findViewById(R.id.seek_bar_int);
        mSeekBarFloat = (SeekBar) mSeekBarView.findViewById(R.id.seek_bar_float);
        mSeekBarValue = (EditText) mSeekBarView.findViewById(R.id.seek_bar_value);
        mSeekBarLabel = (TextView) mSeekBarView.findViewById(R.id.seek_bar_label);

        mSeekBarLabel.setText(mElementModel.getName());

        mSeekBarLabel.setText(mElementModel.getName());


        mSeekBarInt.setMax((int) mMaxValue);
        mSeekBarFloat.setMax(99);

        mSeekBarInt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarIntValue = progress;
                mSeekBarValue.setText((mSeekBarIntValue + mSeekBarFloatValue) + "");
                mListener.onChange(RFSeekBar.this, getData());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarFloat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarFloatValue = (progress / 100f);
                mSeekBarValue.setText((mSeekBarIntValue + mSeekBarFloatValue) + "");
                mListener.onChange(RFSeekBar.this, getData());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setSeekBarPositions(mSeekBarValue.getText().toString());
                mSeekBarValue.setSelection(mSeekBarValue.getText().toString().length());
                mListener.onChange(RFSeekBar.this, getData());
            }
        });

        setUp(mSeekBarView);
    }


    private void setSeekBarPositions(String value) {

        if (value == null || value.trim().length() == 0) {
            mSeekBarInt.setProgress(0);
            mSeekBarFloat.setProgress(0);
            return;
        }

        try {
            float seekBarValue = Float.parseFloat(value);
            int seekBarIntValue = (int) seekBarValue;
            int seekBarFloatValue = (int) ((seekBarValue - seekBarIntValue) * 100);
            mSeekBarInt.setProgress(seekBarIntValue);
            mSeekBarFloat.setProgress(seekBarFloatValue);
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public String getData() {
        return mSeekBarValue.getText().toString();
    }

    @Override
    protected void setData(String data) {
        setSeekBarPositions(data);
    }

    @Override
    public void setError(String error) {
        mSeekBarValue.setError(error);
    }
}
