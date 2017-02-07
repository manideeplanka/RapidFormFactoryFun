package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;

/**
 * Created by kkalluri on 2/4/2016.
 */
public class RFTextView extends RFBaseElement {
    private TextView mTextView;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private String mTitleType = RFConstants.FC_FIELD_TITLE;
    private String mPrevData;

    public RFTextView(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFTextView(new TextView(context));
        init();
    }

    public RFTextView(Context context, RFElementModel elementModel, String titleType) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFTextView(new TextView(context));
        setTitleType(titleType);
        init();
    }

    // All the initializations for the date picker will be present in the init() method
    // setUp() will be called in the init method
    private void init() {
        setUp(mTextView);
        mTextView.setText(mElementModel.getName());

        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListener.onChange(RFTextView.this, getData());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public String getData() {
        return mTextView.getText().toString();
    }

    @Override
    protected void setData(String data) {

        mTextView.setText(data);
    }

    @Override
    public void setError(String error) {

    }

    private void setElementModel(RFElementModel elementModel) {
        mElementModel = elementModel;
    }

    private void setRFTextView(TextView textView) {
        mTextView = textView;
    }

    public String getTitleType() {
        return mTitleType;
    }

    public void setTitleType(String titleType) {
        this.mTitleType = titleType;
    }
}
