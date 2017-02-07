package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rba.ui.MaterialEditText;

/**
 * Created by cdara on 01-03-2016.
 */
public class RFEmail extends RFEditText {

    private MaterialEditText mMaterialEditText;
    private RFElementEventListener mListener;
    private RFElementModel mElementModel;
    private Context mContext;

    public RFEmail(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        init();
    }

    @Override
    protected void setUp(View view) {
        super.setUp(view);
        mMaterialEditText = (MaterialEditText) view;
    }

    // All the initializations for the email will be present in the init() method
    // setUp() will be called in the init method
    public void init() {
        mListener = getEventListeners();
        mElementModel = getElementModel();

        mMaterialEditText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        mMaterialEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    mListener.onBeginEditing(RFEmail.this);
                } else {
                    mListener.onEndEditing(RFEmail.this);

                    if (!RFUtils.isValidEmail(mMaterialEditText.getText().toString())) {
                        mMaterialEditText.setErrorColor(Color.RED);
                        mElementModel.setErrorMessage(RFUtils.getString(mContext, R.string.invalid_email));
                        mMaterialEditText.setError(mElementModel.getErrorMessage());
                    }

                }
            }
        });
    }
}
