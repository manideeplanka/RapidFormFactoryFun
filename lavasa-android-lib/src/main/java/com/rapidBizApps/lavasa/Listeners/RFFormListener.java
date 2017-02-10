package com.rapidBizApps.lavasa.Listeners;


/**
 * Created by cdara on 09-12-2015.
 */

import android.support.annotation.Nullable;

/**
 * Use: This contains the listeners of the form class.
 */
public interface RFFormListener {

    // This method will be called while we encounter parser error.
    // error - JSON object format
    void onParseError(String error);

    // This method will be called before rendering the form.
    void onBeforeFormRender();

    // This should be called after loading the first page of the form.
    void onLoad();

    /**
     * This method will be called if there is any change in the data of the view.
     *
     * @param fieldJsonResponse this json string . The format is {"keyname":{"data":"","error":""}}
     */
    void onChange(String fieldJsonResponse);

    // This will be called on every page change
    void onPageChange();

    // this will be called when data is to be dynamically loaded from the app rather than from static json file
    void loadDynamicData(String jsonFieldKey, @Nullable String filter, FoundDynamicDataCallback callback);

}
