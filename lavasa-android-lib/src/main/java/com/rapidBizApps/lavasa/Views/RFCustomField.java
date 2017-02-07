package com.rapidBizApps.lavasa.Views;

import android.content.Context;

import com.rapidBizApps.lavasa.Models.RFBaseModel;

/**
 * Created by kkalluri on 3/8/2016.
 */

/**
 * Custom fields will extend RFCustomField
 */
public abstract class RFCustomField extends RFBaseElement {

    public RFCustomField(Context context, RFBaseModel model) {
        super(context, model);
    }
}
