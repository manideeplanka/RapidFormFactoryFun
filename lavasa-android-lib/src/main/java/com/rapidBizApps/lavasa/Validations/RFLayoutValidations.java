package com.rapidBizApps.lavasa.Validations;

import com.rapidBizApps.lavasa.Constants.RFLayoutConstants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by cdara on 30-11-2015.
 */

/**
 * Use: This contains the validation related to the layout properties.
 */
public final class RFLayoutValidations {

    /**
     * Use: Used for validating the given float value in the Json
     *
     * @param data float value of the form element
     * @return returns true if the data is float else false
     */
    public static boolean isValidFloat(String data) {
        try {
            Float.parseFloat(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Use: Used for validating the layout alignment
     *
     * @param alignment alignment of the form element
     * @return returns true if the alignment is valid else false
     */
    public static boolean isValidAlignment(String alignment) {
        switch (alignment.toLowerCase()) {
            case RFLayoutConstants.LC_LEFT:
                return true;
            case RFLayoutConstants.LC_RIGHT:
                return true;
            case RFLayoutConstants.LC_TOP:
                return true;
            case RFLayoutConstants.LC_BOTTOM:
                return true;
            case RFLayoutConstants.LC_CENTER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Use: Used for validating the given padding type in the Json
     *
     * @param padding padding type
     * @return returns true if the padding is valid else false
     */
    public static boolean isValidPadding(String padding) {
        try {
            JSONObject paddingJsonObject = new JSONObject(padding);
            JSONArray paddingList = paddingJsonObject.names();
            boolean isInvalidData = false;
            for (int i = 0; i < paddingList.length(); i++) {
                String paddingType = paddingList.getString(i);

                switch (paddingType.toLowerCase()) {

                    case RFLayoutConstants.LC_LEFT_PADDING:
                    case RFLayoutConstants.LC_RIGHT_PADDING:
                    case RFLayoutConstants.LC_TOP_PADDING:
                    case RFLayoutConstants.LC_BOTTOM_PADDING:
                        // This will verifies whether the mentioned padding is valid float or not.
                        if (!isValidFloat(paddingJsonObject.getString(paddingType))) {
                            isInvalidData = true;
                        }
                        break;
                }
                if (isInvalidData) {
                    break;
                }
            }

            if (isInvalidData) {
                return false;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}