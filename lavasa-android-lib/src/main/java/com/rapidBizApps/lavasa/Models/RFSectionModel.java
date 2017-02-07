package com.rapidBizApps.lavasa.Models;

import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cdara on 20-01-2016.
 */

/**
 * Use: Section information is stored in this model
 */
public final class RFSectionModel extends RFBaseModel {

    private ArrayList<RFElementModel> fields;

    // String - > FieldKeyName Integer - > field position w.r.t section
    private HashMap<String, Integer> fieldPositionDetails;

    private RFPageModel page;
    private RFElementModel sectionTitleField;


    public RFSectionModel(RFPageModel page, String id, String name, ArrayList<RFElementModel> fields) {

        setPage(page);
        setId(id);
        setName(name);
        setFields(fields);
        setSectionTitleField(new RFElementModel(name, RFElementTypeConstants.ETC_TITLE));

        HashMap<String, Integer> fieldPositionDetails = new HashMap<>();
        setFieldPositionDetails(fieldPositionDetails);

        layout.setDefaultSectionLayoutProperties();
        style.setDefaultSectionStyleProperties();
    }

    public RFSectionModel(RFPageModel page, String id, ArrayList<RFElementModel> fields) {
        this(page, id, id, fields);
    }

    public ArrayList<RFElementModel> getFields() {
        return fields;
    }

    public void setFields(ArrayList<RFElementModel> fields) {
        this.fields = fields;
    }

    public HashMap<String, Integer> getFieldPositionDetails() {
        return fieldPositionDetails;
    }

    public void setFieldPositionDetails(HashMap<String, Integer> fieldPositionDetails) {
        this.fieldPositionDetails = fieldPositionDetails;
    }

    public RFPageModel getPage() {
        return page;
    }

    public void setPage(RFPageModel page) {
        this.page = page;
    }

    public RFElementModel getSectionTitleField() {
        return sectionTitleField;
    }

    public void setSectionTitleField(RFElementModel sectionTitleField) {
        this.sectionTitleField = sectionTitleField;
    }

    /**
     * This method is used for returning the section response
     *
     * @return
     */
    public JSONObject getResponseData() {

        boolean isStructured = page.getForm().isStructured();
        JSONObject responseJson = new JSONObject();
        JSONObject errorJson = new JSONObject();
        JSONObject dataJson = new JSONObject();

        if (isStructured) {
            try {

                JSONArray sectionArray = new JSONArray();

                for (int i = 0; i < fields.size(); i++) {
                    JSONObject fieldDataJson = new JSONObject();

                    RFElementModel elementModel = fields.get(i);
                    errorJson.put(elementModel.getId(), elementModel.getErrorMessage());
                    fieldDataJson.put(elementModel.getId(), elementModel.getValue());

                    if (fieldDataJson.names() != null && fieldDataJson.names().length() != 0) {
                        sectionArray.put(fieldDataJson);
                    }
                }

                dataJson.put(id, sectionArray);
                responseJson.put(RFConstants.FC_DATA, dataJson);
                responseJson.put(RFConstants.FC_ERRORS, errorJson);

            } catch (JSONException e) {
                Log.e("RFSection","getResponseData"+ e);
            }
        } else {

            try {
                for (int i = 0; i < fields.size(); i++) {
                    RFElementModel elementModel = fields.get(i);
                    if(elementModel.getStyle() != null && elementModel.getStyle().isVisibility()) {
                        String temp = fields.get(i).getErrorMessage();
                        if (temp != null && temp.length() > 0) {
                            errorJson.put(elementModel.getId(), elementModel.getErrorMessage());
                        }
                    }
                    dataJson.put(elementModel.getId(), elementModel.getValue());
                }
                responseJson.put(RFConstants.FC_DATA, dataJson);
                responseJson.put(RFConstants.FC_ERRORS, errorJson);

            } catch (JSONException e) {
                Log.e("RFSection ","getResponseData"+ e);
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

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getId().equals(keyName)) {
                String response = this.fields.get(i).getValue();
                JSONObject responseJson = new JSONObject();
                JSONObject dataJsonObject = new JSONObject();
                JSONObject errorJsonObject = new JSONObject();
                try {
                    dataJsonObject.put(fields.get(i).id, response);
                    errorJsonObject.put(fields.get(i).id, fields.get(i).getErrorMessage());
                    responseJson.put(RFConstants.FC_DATA, dataJsonObject);
                    responseJson.put(RFConstants.FC_ERRORS, errorJsonObject);
                    return responseJson;
                } catch (JSONException e) {
                    Log.e("RFSectionModel ","getResponseData"+ e.toString());
                }
            }
        }
        return null;
    }
}
