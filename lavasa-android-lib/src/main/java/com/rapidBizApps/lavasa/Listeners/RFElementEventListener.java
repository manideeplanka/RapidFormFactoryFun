package com.rapidBizApps.lavasa.Listeners;

import com.rapidBizApps.lavasa.Views.RFBaseElement;

/**
 * Created by cdara on 25-01-2016.
 */

/**
 * Use: All the default elements and the custom Elements/ custom fields classes have to implement this method.
 */
public interface RFElementEventListener {

    /**
     * onChange will be called when there is any change in the data of the field.
     * In onChange() we should set field data to the element model.
     *
     * @param element
     * @param data
     */
    public void onChange(RFBaseElement element, String data);

    /**
     * This method will be called onFocus() of edit text type elements
     */
    public void onBeginEditing(RFBaseElement element);

    /**
     * This method will be called onFocusLost() of edit text type elements
     */
    public void onEndEditing(RFBaseElement element);

}
