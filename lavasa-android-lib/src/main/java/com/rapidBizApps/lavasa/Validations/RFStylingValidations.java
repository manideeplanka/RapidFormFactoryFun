package com.rapidBizApps.lavasa.Validations;

import com.rapidBizApps.lavasa.Constants.RFStyleConstants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by cdara on 22-12-2015.
 */

/**
 * Use: This is used for validating the styling properties.
 */
public final class RFStylingValidations {

    /**
     * Use: Used for validating the background
     *
     * @param object
     * @return
     */
    public static boolean isValidBackground(Object object) {

        // TODO: Currently  added validation for only background color. Need to add validations for other properties.

        try {
            JSONObject jsonObject = (JSONObject) object;
            JSONArray backgroundProperties = jsonObject.names();

            for (int i = 0; i < backgroundProperties.length(); i++) {
                String property = backgroundProperties.getString(i);
                switch (property.toLowerCase()) {
                    case RFStyleConstants.SC_BACKGROUND_COLOR:
                        String rgbColor = jsonObject.getString(property);
                        boolean isValidColor = isValidColor(rgbColor);
                        if (!isValidColor) {
                            return false;
                        }
                        break;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Use: Validates the rgb format of the color
     *
     * @param rgb
     * @return
     */
    public static boolean isValidColor(String rgb) {
        if (rgb.length() == 0) {
            return false;
        }
        rgb = rgb.substring(1, rgb.length() - 1);

        String[] rgbValues = rgb.split(",");

        if (rgbValues == null || rgbValues.length != 3) {
            return false;
        }

        int r, g, b;

        try {
            r = Integer.parseInt(rgbValues[0]);
            g = Integer.parseInt(rgbValues[1]);
            b = Integer.parseInt(rgbValues[2]);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Use: Validates whether the font is valid or not
     *
     * @param object
     * @return
     */

    // TODO: Need to add validation for typeface
    public static boolean isValidFont(Object object) {
        try {

            JSONObject jsonObject = (JSONObject) object;
            JSONArray fontProperties = jsonObject.names();

            for (int i = 0; i < fontProperties.length(); i++) {

                String property = fontProperties.getString(i);

                switch (property.toLowerCase()) {
                    case RFStyleConstants.SC_FONT_SIZE:
                    case RFStyleConstants.SC_TITLE_FONT_SIZE:
                        boolean isValidSize = RFLayoutValidations.isValidFloat(jsonObject.getString(property));
                        if (!isValidSize) {
                            return false;
                        }
                        break;
                    case RFStyleConstants.SC_FONT_COLOR:
                    case RFStyleConstants.SC_TITLE_FONT_COLOR:
                        boolean isValidColor = isValidColor(jsonObject.getString(property));
                        if (!isValidColor) {
                            return false;
                        }
                        break;
                    case RFStyleConstants.SC_FONT_NAME:
                        break;
                    case RFStyleConstants.SC_FONT_STYLE:
                    case RFStyleConstants.SC_TITLE_FONT_STYLE:
                        boolean isValidFontStyle = isValidFontStyle(jsonObject.getString(property));
                        if (!isValidFontStyle) {
                            return false;
                        }
                        break;
                }

            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Use: verifies whether the font style is valid or not
     *
     * @return
     */
    public static boolean isValidFontStyle(String style) {
        switch (style.toLowerCase()) {
            case RFStyleConstants.SC_BOLD:
            case RFStyleConstants.SC_ITALIC:
            case RFStyleConstants.SC_NORMAL:
                return true;
            default:
                return false;
        }
    }
}
