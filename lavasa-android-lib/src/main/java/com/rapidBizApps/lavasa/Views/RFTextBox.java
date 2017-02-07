package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.view.View;

import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rba.ui.MaterialEditText;

/**
 * Created by cdara on 01-03-2016.
 */
public class RFTextBox extends RFEditText {

    private MaterialEditText mMaterialEditText;

    public RFTextBox(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mMaterialEditText.setSingleLine(false);
    }

    @Override
    protected void setUp(View view) {
        super.setUp(view);
        mMaterialEditText = (MaterialEditText) view;
    }

}
