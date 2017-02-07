package com.rapidBizApps.lavasa.Validations;

import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;
import com.rapidBizApps.lavasa.ParseAndRender.RFRegister;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cdara on 02-12-2015.
 */

/**
 * Use: This contains the validation related to the form json.
 */
public final class RFJsonValidations {
    /**
     * Use: Used for validating the given type in the Json
     *
     * @param type type of the form element
     * @return returns true if the type is valid else false
     */
    public static boolean isValidType(String type, RFRegister register) {

        type = type.toLowerCase();

        if (type.equals(RFElementTypeConstants.ETC_TEXT.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_TEXT_BOX.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_EMAIL.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_PASSWORD.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_NUMBER.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_PHONE.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_DATE.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_TIME.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_CREDIT_CARD.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_TITLE.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_SINGLE_SELECTION.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_MULTI_SELECTION.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_SWITCH.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_SIGNATURE.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_SEEK_BAR.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_IMAGE.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_IMAGE_PICKER.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_LOCATION.toLowerCase())
                || type.equals(RFElementTypeConstants.ETC_NUMBER_DECIMAL.toLowerCase())) {

            return true;
        } else {
            return register.checkForRegisteredElement(type);
        }
    }

    /**
     * Use: Used for validating the given field data in the form json
     *
     * @return returns true if the data is valid else false
     */
    public static boolean isValidData(Object obj) {
        try {
            JSONArray array = (JSONArray) obj;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This will check whether the data is boolean data or not.
     *
     * @param jsonObject
     * @param keyName
     * @return
     */
    public static boolean isValidBoolean(JSONObject jsonObject, String keyName) {
        try {
            Boolean.parseBoolean(jsonObject.getString(keyName));
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * This will check whether the object is json or not
     *
     * @param object JSON element
     * @return returns true if the json element is JSONObject else false
     */
    public static boolean isJSON(Object object) {
        try {
            JSONObject jsonObject = (JSONObject) object;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This will check whether the object is json array or not
     *
     * @param object JSON element
     * @return returns true if the json array is JSONObject else false
     */
    public static boolean isJSONArray(Object object) {
        try {
            JSONArray jsonArray = (JSONArray) object;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
