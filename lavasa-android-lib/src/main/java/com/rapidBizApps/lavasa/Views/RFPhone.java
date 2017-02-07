package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.text.InputType;
import android.view.View;

import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rba.ui.MaterialEditText;

/**
 * Created by cdara on 01-03-2016.
 */
public class RFPhone extends RFEditText {

    private MaterialEditText mMaterialEditText;

    public RFPhone(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        init();
    }

    @Override
    protected void setUp(View view) {
        super.setUp(view);
        mMaterialEditText = (MaterialEditText) view;
    }

    // All the initializations for the number will be present in the init() method
    // setUp() will be called in the init method
    public void init() {
        mMaterialEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
