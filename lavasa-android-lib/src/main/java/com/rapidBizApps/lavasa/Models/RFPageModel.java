package com.rapidBizApps.lavasa.Models;

import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;
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
 * Use: Page information is stored in this model
 */
public final class RFPageModel extends RFBaseModel {

    private ArrayList<RFSectionModel> sections;

    // String - > SectionKeyName Integer - > RFSectionModel position w.r.t page
    private HashMap<String, Integer> sectionPositionDetails;

    private RFElementModel pageTitleField;
    private RFFormModel form;

    public RFPageModel(RFFormModel form, String id, String name, ArrayList<RFSectionModel> sections) {
        setForm(form);
        setId(id);
        setName(name);
        setSections(sections);

        HashMap<String, Integer> sectionPositionDetails = new HashMap<>();
        setSectionPositionDetails(sectionPositionDetails);

        setPageTitleField(new RFElementModel(name,RFElementTypeConstants.ETC_TITLE,"page_title_id"));

        layout.setDefaultPageLayoutProperties();
        style.setDefaultPageStyleProperties();
    }

    public RFPageModel(RFFormModel form, String id, ArrayList<RFSectionModel> sections) {
        this(form, id, id, sections);
    }

    public ArrayList<RFSectionModel> getSections() {
        return sections;
    }

    public void setSections(ArrayList<RFSectionModel> sections) {
        this.sections = sections;
    }

    public HashMap<String, Integer> getSectionPositionDetails() {
        return sectionPositionDetails;
    }

    public void setSectionPositionDetails(HashMap<String, Integer> sectionPositionDetails) {
        this.sectionPositionDetails = sectionPositionDetails;
    }

    public RFElementModel getPageTitleField() {
        return pageTitleField;
    }

    public void setPageTitleField(RFElementModel pageTitleField) {
        this.pageTitleField = pageTitleField;
    }

    public RFFormModel getForm() {
        return form;
    }

    public void setForm(RFFormModel form) {
        this.form = form;
    }

    /**
     * This method is used for returning the page response
     *
     * @return
     */
    public JSONObject getResponseData() {

        boolean isStructured = form.isStructured();

        JSONObject responseJson = new JSONObject();
        JSONObject errorJson = new JSONObject();
        JSONObject dataJson = new JSONObject();

        if (isStructured) {

            try {

                JSONArray pageArray = new JSONArray();

                for (int i = 0; i < sections.size(); i++) {

                    JSONObject sectionResponseJson = sections.get(i).getResponseData();
                    JSONObject sectionDataJson = sectionResponseJson.getJSONObject(RFConstants.FC_DATA);
                    JSONObject sectionErrorJson = sectionResponseJson.getJSONObject(RFConstants.FC_ERRORS);

                    // In this if condition, if the section is default section we are adding elements directly to page array.
                    if (sections.get(i).getId().contains(RFConstants.FC_DEFAULT_SECTION)) {

                        if (!sectionDataJson.isNull(sections.get(i).getId())) {

                            JSONArray array = sectionDataJson.getJSONArray(sections.get(i).getId());

                            for (int j = 0; j < array.length(); j++) {
                                pageArray.put(array.get(j));
                            }
                        }

                    } else {
                        pageArray.put(sectionDataJson);
                    }

                    RFUtils.mergeJsons(errorJson, sectionErrorJson);
                }

                dataJson.put(id, pageArray);

                responseJson.put(RFConstants.FC_DATA, dataJson);
                responseJson.put(RFConstants.FC_ERRORS, errorJson);

            } catch (JSONException e) {
                Log.e("RFPageModel getResponseData", e.toString());
            }
        } else {

            try {

                for (int i = 0; i < sections.size(); i++) {

                    JSONObject sectionResponseJson = sections.get(i).getResponseData();
                    JSONObject sectionDataJson = sectionResponseJson.getJSONObject(RFConstants.FC_DATA);
                    JSONObject sectionErrorJson = sectionResponseJson.getJSONObject(RFConstants.FC_ERRORS);

                    RFUtils.mergeJsons(dataJson, sectionDataJson);
                    RFUtils.mergeJsons(errorJson, sectionErrorJson);

                }

                responseJson.put(RFConstants.FC_DATA, dataJson);
                responseJson.put(RFConstants.FC_ERRORS, errorJson);

            } catch (JSONException e) {
                Log.e("RFPageModel getResponseData", e.toString());
            }

        }

        return responseJson;
    }

    /**
     * This method will return the response related to particular key
     *
     * @param keyName
     * @return
     */
    public JSONObject getResponseData(String keyName) {
        if (id.equals(keyName)) {
            return this.getResponseData();
        }

        for (int i = 0; i < sections.size(); i++) {
            JSONObject response = this.sections.get(i).getResponseData(keyName);
            if (response != null) {
                return response;
            }
        }

        return null;
    }
}
