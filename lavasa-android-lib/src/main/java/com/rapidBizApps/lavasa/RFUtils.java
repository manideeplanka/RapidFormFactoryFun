package com.rapidBizApps.lavasa;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.rapidBizApps.lavasa.Constants.RFStyleConstants;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by cdara on 01-12-2015.
 */

/**
 * Use: This contains the utils which are used in different classes.
 */
public final class RFUtils {

    public static final String TAG = "RFUtils";

    /**
     * Use: Used for converting the pixel to dp
     *
     * @param px
     * @param context context of the activity / fragment
     * @return returns the dp value of the given pixel
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * This method is used for verifying whether the device is tablet or not.
     *
     * @param context Context of the application/fragment
     * @return true return true if the device is tablet else false
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * This is used for calculating the screen width
     *
     * @param context context Context of the application/fragment
     * @return returns the width of the screen
     */
    public static float getScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * This is used for converting percent to dp
     *
     * @param widthInPercent
     * @param screenWidth
     * @return returns the width in dp
     */
    public static float convertPercentToDP(float widthInPercent, float screenWidth) {
        return (screenWidth * widthInPercent) / 100;
    }

    /**
     * Use: Used for converting the RGB to color
     *
     * @param rgb
     * @return
     */
    public static int getColor(String rgb) {

        rgb = rgb.substring(1, rgb.length() - 1);

        String[] rgbValues = rgb.split(",");

        int r, g, b;

        r = Integer.parseInt(rgbValues[0]);
        g = Integer.parseInt(rgbValues[1]);
        b = Integer.parseInt(rgbValues[2]);

        int color = Color.rgb(r, g, b);
        return color;
    }

    /**
     * Use: Used for returning the  font  style
     *
     * @param style
     * @return
     */
    public static int getFontStyle(String style) {
        switch (style.toLowerCase()) {
            case RFStyleConstants.SC_BOLD:
                return Typeface.BOLD;
            case RFStyleConstants.SC_ITALIC:
                return Typeface.ITALIC;
            default:
                return Typeface.NORMAL;
        }
    }

    /**
     * This is used for returning particular visibility property
     *
     * @return
     */
    public static int getVisibility(boolean visibility) {
        if (visibility) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    /**
     * This will return the string belongs to particular id.
     *
     * @param context
     * @param id
     * @return
     */
    public static String getString(Context context, int id) {
        return context.getString(id);
    }

    /**
     * This method will merges two different jsons
     *
     * @param firstJson
     * @param secondJson
     * @return
     */
    public static JSONObject mergeJsons(JSONObject firstJson, JSONObject secondJson) {
        if (firstJson == null) {
            return secondJson;
        }
        if (secondJson == null) {
            return firstJson;
        }

        try {
            JSONArray array = secondJson.names();

            if (array == null) {
                return firstJson;
            }
            for (int i = 0; i < array.length(); i++) {
                String keyName = array.getString(i);
                firstJson.put(keyName, secondJson.get(keyName));
            }

        } catch (JSONException e) {
            Log.e(TAG + " mergeJsons", e.toString());
        }

        return firstJson;
    }

    /**
     * Use : Used for verifying the email format
     *
     * @param email
     * @return
     */
    public final static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    /**
     * This method will convert the native object to json object.
     *
     * @param nativeObject
     * @return
     */
    public static JSONObject convertNativeObjectToJsonObject(NativeObject nativeObject) {
        Set set = nativeObject.keySet();
        Iterator iterator = set.iterator();

        JSONObject jsonObject = new JSONObject();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            try {
                jsonObject.put(key, nativeObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonObject;
    }

    /**
     * This will throw parse error
     *
     * @param formListener
     */
    public static void throwParseError(RFFormListener formListener, String errorMessage) {
        if (formListener != null) {
            formListener.onParseError(errorMessage);
        }
    }

    /**
     * This will check whether the class object is instance of parent class.
     *
     * @param parentClass
     * @param classObject
     * @return
     */
    public static boolean isInstance(Class parentClass, Object classObject) {
        try {
            parentClass.cast(classObject);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static boolean isLandscape(int orientation) {
        return orientation >= (90 - 360) && orientation <= (90 + 360);
    }

    public static boolean isPortrait(int orientation) {
        return (orientation >= (360 - 360) && orientation <= 360) || (orientation >= 0 && orientation <= 360);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }



    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }
}


