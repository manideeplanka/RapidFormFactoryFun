package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rba.ui.MaterialEditText;

/**
 * Created by cdara on 27-01-2016.
 */

/**
 * If we extends RFEditText before setting the input type listeners will be added to the view.
 * Because of that onChange will be called when we set input type to password. To restrict that we extended RFBaseElement and we set input type before adding listener.
 */
public class RFPassword extends RFBaseElement {

    private MaterialEditText mMaterialEditText;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;

    public RFPassword(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFEditText(new MaterialEditText(context));
        init();
    }

    // All the initializations for the edit text will be present in the init() method
    // setUp() will be called in the init method
    // for material edit text we need to follow properties order. Before setting error we need to set all the properties
    private void init() {

        mMaterialEditText.setSingleLine();

        mMaterialEditText.setHint(mElementModel.getName());

        mMaterialEditText.setFocusable(true);

        mMaterialEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mMaterialEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // we cannot set error in onTextCHange for material edittext. That is we are invoking onChange in afterTextChange instead of onTextChanged
                mListener.onChange(RFPassword.this, getData());
            }
        });

        mMaterialEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    mListener.onBeginEditing(RFPassword.this);

                    // Below if condition is for setting empty string if it is focused and not entering any data
                    if (mElementModel.getValue() == null) {
                        mElementModel.setValue(mMaterialEditText.getText().toString());
                    }
                } else {
                    mListener.onEndEditing(RFPassword.this);
                }
            }
        });

        setUp(mMaterialEditText);

        mMaterialEditText.setFloatingLabelText(null);
        mMaterialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        mMaterialEditText.setFloatingLabelAnimating(true);

    }

    @Override
    public String getData() {
        return mMaterialEditText.getText().toString();
    }

    @Override
    protected void setData(String data) {

        /**
         * Though we set the properties in the init method. Here we need to set again because these properties are getting overrided.
         * If we didn't set these properties again we won't observe labels when we come from one page to another page.
         */
        mMaterialEditText.setFloatingLabelText(null);
        mMaterialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        mMaterialEditText.setFloatingLabelAnimating(true);

        mMaterialEditText.setText(data);
    }

    @Override
    public void setError(String error) {
        if (mMaterialEditText != null) {
            mMaterialEditText.setError(error);
        }
    }

    public RFElementModel getElementModel() {
        return mElementModel;
    }

    private void setElementModel(RFElementModel elementModel) {
        mElementModel = elementModel;
    }

    private void setRFEditText(MaterialEditText materialEditText) {
        mMaterialEditText = materialEditText;
    }

    public RFElementEventListener getEventListeners() {
        return mListener;
    }
}
