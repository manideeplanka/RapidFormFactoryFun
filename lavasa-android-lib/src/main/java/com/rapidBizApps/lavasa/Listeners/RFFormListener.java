package com.rapidBizApps.lavasa.Listeners;


/**
 * Created by cdara on 09-12-2015.
 */

/**
 * Use: This contains the listeners of the form class.
 */
public interface RFFormListener {

    // This method will be called while we encounter parser error.
    // error - JSON object format
    public void onParseError(String error);

    // This method will be called before rendering the form.
    public void onBeforeFormRender();

    // This should be called after loading the first page of the form.
    public void onLoad();

    /**
     * This method will be called if there is any change in the data of the view.
     *
     * @param fieldJsonResponse this json string . The format is {"keyname":{"data":"","error":""}}
     */
    public void onChange(String fieldJsonResponse);

    // This will be called on every page change
    public void onPageChange();
}
