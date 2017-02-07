package com.rapidBizApps.lavasa.Models;

import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;

import java.util.LinkedHashMap;

/**
 * Created by cdara on 20-01-2016.
 */

/**
 * Use: This is used for storing the fields related data.
 */
public final class RFElementModel extends RFBaseModel {

    // This contains the version of the Forms Library
    private String version;
    private String type;
    private String errorMessage;
    private String value;
    private RFSectionModel section;
    // jsonData(id,data)key name  is id and data is jsonData
    private LinkedHashMap<String, String> jsonData;

    private static final String TAG = "RFElementModel";


    public RFElementModel(RFSectionModel section, String id, String name, String type) {
        setSection(section);
        setId(id);
        setName(name);
        setType(getType(type));

        layout.setDefaultFieldLayoutProperties(this);
        style.setDefaultFieldStyleProperties(this);
    }

    public RFElementModel(String name, String type) {
        setName(name);
        setType(getType(type));

        layout.setDefaultFieldLayoutProperties(this);
        style.setDefaultFieldStyleProperties(this);
    }

    public RFElementModel(String name, String type,String id) {
        setName(name);
        setType(getType(type));
        setId(id);
        layout.setDefaultFieldLayoutProperties(this);
        style.setDefaultFieldStyleProperties(this);
    }

    public RFElementModel(RFSectionModel section, String id, String name, String type, LinkedHashMap<String, String> data) {
        this(section, id, name, type);
        setJsonData(data);
    }

    public RFSectionModel getSection() {
        return section;
    }

    public void setSection(RFSectionModel section) {
        this.section = section;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkedHashMap<String, String> getJsonData() {
        return jsonData;
    }

    public void setJsonData(LinkedHashMap<String, String> jsonData) {
        this.jsonData = jsonData;
    }

    /**
     * In the json the type is case insensitive. If we maintain insensitive in storing also in the RFSEction model we need to use
     * if else if instead of switch. Switch is more faster than if else if. To maintain that here we are converting the type to same form as we defined.
     *
     * @param fieldType
     * @return
     */
    private String getType(String fieldType) {

        String type = fieldType.toLowerCase();

        if (type.equals(RFElementTypeConstants.ETC_TEXT.toLowerCase())) {
            return RFElementTypeConstants.ETC_TEXT;
        } else if (type.equals(RFElementTypeConstants.ETC_TEXT_BOX.toLowerCase())) {
            return RFElementTypeConstants.ETC_TEXT_BOX;
        } else if (type.equals(RFElementTypeConstants.ETC_EMAIL.toLowerCase())) {
            return RFElementTypeConstants.ETC_EMAIL;
        } else if (type.equals(RFElementTypeConstants.ETC_NUMBER.toLowerCase())) {
            return RFElementTypeConstants.ETC_NUMBER;
        } else if (type.equals(RFElementTypeConstants.ETC_PASSWORD.toLowerCase())) {
            return RFElementTypeConstants.ETC_PASSWORD;
        } else if (type.equals(RFElementTypeConstants.ETC_PHONE.toLowerCase())) {
            return RFElementTypeConstants.ETC_PHONE;
        } else if (type.equals(RFElementTypeConstants.ETC_DATE.toLowerCase())) {
            return RFElementTypeConstants.ETC_DATE;
        } else if (type.equals(RFElementTypeConstants.ETC_TIME.toLowerCase())) {
            return RFElementTypeConstants.ETC_TIME;
        } else if (type.equals(RFElementTypeConstants.ETC_CREDIT_CARD.toLowerCase())) {
            return RFElementTypeConstants.ETC_CREDIT_CARD;
        } else if (type.equals(RFElementTypeConstants.ETC_TITLE.toLowerCase())) {
            return RFElementTypeConstants.ETC_TITLE;
        } else if (type.equals(RFElementTypeConstants.ETC_SINGLE_SELECTION.toLowerCase())) {
            return RFElementTypeConstants.ETC_SINGLE_SELECTION;
        } else if (type.equals(RFElementTypeConstants.ETC_MULTI_SELECTION.toLowerCase())) {
            return RFElementTypeConstants.ETC_MULTI_SELECTION;
        } else if (type.equals(RFElementTypeConstants.ETC_SWITCH.toLowerCase())) {
            return RFElementTypeConstants.ETC_SWITCH;
        } else if (type.equals(RFElementTypeConstants.ETC_SIGNATURE.toLowerCase())) {
            return RFElementTypeConstants.ETC_SIGNATURE;
        } else if (type.equals(RFElementTypeConstants.ETC_SEEK_BAR.toLowerCase())) {
            return RFElementTypeConstants.ETC_SEEK_BAR;
        } else if (type.equals(RFElementTypeConstants.ETC_IMAGE.toLowerCase())) {
            return RFElementTypeConstants.ETC_IMAGE;
        } else if (type.equals(RFElementTypeConstants.ETC_IMAGE_PICKER.toLowerCase())) {
            return RFElementTypeConstants.ETC_IMAGE_PICKER;
        } else if (type.equals(RFElementTypeConstants.ETC_LOCATION.toLowerCase())) {
            return RFElementTypeConstants.ETC_LOCATION;
        } else if (type.equals(RFElementTypeConstants.ETC_NUMBER_DECIMAL.toLowerCase())) {
            return RFElementTypeConstants.ETC_NUMBER_DECIMAL;
        }

        return fieldType;
    }

}
