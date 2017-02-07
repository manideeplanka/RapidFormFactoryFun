package com.rapidBizApps.lavasa.Models;

import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.RFUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cdara on 20-01-2016.
 */

/**
 * Use: Form information is stored in this model.
 */
public final class RFFormModel extends RFBaseModel {

    // This contains the version of the Forms Library
    private String version;

    ;
    private ArrayList<RFPageModel> pages;
    // String - > PageKeyName Integer - > RFSectionModel position w.r.t form
    private HashMap<String, Integer> pagePositionDetails;
    private RFElementModel formTitleField;
    private RFPagesStates currentPageState = RFPagesStates.BEGIN;
    private boolean isStructured = false;

    public RFFormModel(ArrayList<RFPageModel> pages) {
        setPages(pages);
        setPagePositionDetails(new HashMap<String, Integer>());

        layout.setDefaultFormLayoutProperties();
        style.setDefaultFormStyleProperties();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<RFPageModel> getPages() {
        return pages;
    }

    public void setPages(ArrayList<RFPageModel> pages) {
        this.pages = pages;
    }

    public HashMap<String, Integer> getPagePositionDetails() {
        return pagePositionDetails;
    }

    public void setPagePositionDetails(HashMap<String, Integer> pagePositionDetails) {
        this.pagePositionDetails = pagePositionDetails;
    }

    public RFElementModel getFormTitleField() {
        return formTitleField;
    }

    public void setFormTitleField(RFElementModel formTitleField) {
        this.formTitleField = formTitleField;
    }

    public RFPagesStates getCurrentPageState() {
        return currentPageState;
    }

    public void setCurrentPageState(RFPagesStates currentPageState) {
        this.currentPageState = currentPageState;
    }

    public boolean isStructured() {
        return isStructured;
    }

    public void setStructured(boolean structured) {
        isStructured = structured;
    }

    /**
     * This method is used for returning the page response
     *
     * @return
     */
    public JSONObject getResponseData() {
        JSONObject responseJson = new JSONObject();
        JSONObject errorJson = new JSONObject();
        JSONObject dataJson = new JSONObject();

        if (isStructured) {
            try {

                JSONArray formArray = new JSONArray();

                for (int i = 0; i < pages.size(); i++) {

                    JSONObject pageResponseJson = pages.get(i).getResponseData();
                    JSONObject pageDataJson = pageResponseJson.getJSONObject(RFConstants.FC_DATA);
                    JSONObject pageErrorJson = pageResponseJson.getJSONObject(RFConstants.FC_ERRORS);

                    formArray.put(pageDataJson);
                    RFUtils.mergeJsons(errorJson, pageErrorJson);

                }

                dataJson.put(id, formArray);
                responseJson.put(RFConstants.FC_DATA, dataJson);
                responseJson.put(RFConstants.FC_ERRORS, errorJson);

            } catch (JSONException e) {
                Log.e("RFFormModel getResponseData", e.toString());
            }


        } else {

            try {

                for (int i = 0; i < pages.size(); i++) {

                    JSONObject pageResponseJson = pages.get(i).getResponseData();
                    JSONObject pageDataJson = pageResponseJson.getJSONObject(RFConstants.FC_DATA);
                    JSONObject pageErrorJson = pageResponseJson.getJSONObject(RFConstants.FC_ERRORS);

                    RFUtils.mergeJsons(dataJson, pageDataJson);
                    RFUtils.mergeJsons(errorJson, pageErrorJson);

                }

                responseJson.put(RFConstants.FC_DATA, dataJson);
                responseJson.put(RFConstants.FC_ERRORS, errorJson);

            } catch (JSONException e) {
                Log.e("RFFormModel getResponseData", e.toString());
            }
        }

        return responseJson;
    }

    /**
     * This will return the response related to particular key
     *
     * @param keyName
     * @return
     */
    public JSONObject getResponseData(String keyName) {
        if (id.equals(keyName)) {
            return this.getResponseData();
        }

        for (int i = 0; i < pages.size(); i++) {
            JSONObject response = this.pages.get(i).getResponseData(keyName);
            if (response != null) {
                return response;
            }
        }

        return null;
    }
}
