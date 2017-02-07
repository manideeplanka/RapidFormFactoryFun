package com.rapidBizApps.lavasa.ParseAndRender;

import android.content.Context;
import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;
import com.rapidBizApps.lavasa.Constants.RFLayoutConstants;
import com.rapidBizApps.lavasa.Constants.RFStyleConstants;
import com.rapidBizApps.lavasa.Constants.RFValidationConstants;
import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;
import com.rapidBizApps.lavasa.Models.RFBaseModel;
import com.rapidBizApps.lavasa.Models.RFBehaviourModel;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFLayoutModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.Models.RFStyleModel;
import com.rapidBizApps.lavasa.Models.RFValidationModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rapidBizApps.lavasa.Validations.RFJsonValidations;
import com.rapidBizApps.lavasa.Validations.RFLayoutValidations;
import com.rapidBizApps.lavasa.Validations.RFStylingValidations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by cdara on 25-01-2016.
 */

/**
 * Use: This is used for parsing the form json and to store the data in models.
 * While parsing we should consider case insensitive for the properties.
 */
public final class RFParser {

    private final String TAG = getClass().getName();
    private Context mContext;
    private JSONObject mSchemaJson;
    private JSONObject mLayoutJson;
    private JSONObject mStyleJson;
    private JSONObject mValidationJson;
    private JSONObject mBehaviourJson;
    private RFFormListener mFormListener;
    private RFFormModel mForm;
    private RFRegister mRegister;
    private HashMap<String, RFBaseModel> mFormElementModels = new HashMap<>();

    public RFParser(RFController controller) {
        // This will not allow object creation if the controller is null
        if (controller == null) {
            throw new NullPointerException();
        }

        mContext = controller.getContext();
        mFormListener = controller.getFormListener();
        mRegister = controller.getRegister();
    }

    /**
     * This will initiates all the method calls which are used for parsing and storing data.
     *
     * @param formJson
     * @param formData
     * @return
     */
    public RFFormModel parseJson(JSONObject formJson, JSONObject formData) {

        extractJsons(formJson);

        mForm = parseSchema();
        parseLayout();
        parseStyle();
        parseValidation();
        parseBehaviour();
        parseFormData(formData);

        return mForm;
    }

    /**
     * This method is used for extracting the different kinds of Json present in the form Json.
     * This will call onParse error if there is any issue with form json object.
     */
    private void extractJsons(JSONObject formJson) {

        try {

            // Below conditions will check and stores the json if present

            JSONArray properties = formJson.names();

            for (int i = 0; i < properties.length(); i++) {
                String currentProperty = properties.getString(i);

                switch (currentProperty.toLowerCase()) {
                    case RFConstants.FC_SCHEMA:
                        try {
                            mSchemaJson = formJson.getJSONObject(currentProperty);
                        } catch (JSONException e) {
                            RFUtils.throwParseError(mFormListener, RFUtils.getString(mContext, R.string.invalid_value_error) + currentProperty);
                        }
                        break;
                    case RFConstants.FC_LAYOUT:
                        try {
                            mLayoutJson = formJson.getJSONObject(currentProperty);
                        } catch (JSONException e) {
                            RFUtils.throwParseError(mFormListener, RFUtils.getString(mContext, R.string.invalid_value_error) + currentProperty);
                        }
                        break;
                    case RFConstants.FC_STYLES:
                        try {
                            mStyleJson = formJson.getJSONObject(currentProperty);
                        } catch (JSONException e) {
                            RFUtils.throwParseError(mFormListener, RFUtils.getString(mContext, R.string.invalid_value_error) + currentProperty);
                        }
                        break;
                    case RFConstants.FC_VALIDATION:
                        try {
                            mValidationJson = formJson.getJSONObject(currentProperty);
                        } catch (JSONException e) {
                            RFUtils.throwParseError(mFormListener, RFUtils.getString(mContext, R.string.invalid_value_error) + currentProperty);
                        }
                        break;
                    case RFConstants.FC_BEHAVIOUR:
                        try {
                            mBehaviourJson = formJson.getJSONObject(currentProperty);
                        } catch (JSONException e) {
                            RFUtils.throwParseError(mFormListener, RFUtils.getString(mContext, R.string.invalid_value_error) + currentProperty);
                        }
                        break;
                }
            }

            // Belows conditions will check whether there is any empty json mentioned in the form json.
            // If there are empty jsons it will initiates that particular json with null.

            if (mSchemaJson != null && mSchemaJson.names() == null) {
                mSchemaJson = null;
            }

            if (mLayoutJson != null && mLayoutJson.names() == null) {
                mLayoutJson = null;
            }

            if (mStyleJson != null && mStyleJson.names() == null) {
                mStyleJson = null;
            }

            if (mValidationJson != null && mValidationJson.names() == null) {
                mValidationJson = null;
            }

            if (mBehaviourJson != null && mBehaviourJson.names() == null) {
                mBehaviourJson = null;
            }

        } catch (JSONException e) {
            // This will return parse error to user if the mentioned json is incorrect
            RFUtils.throwParseError(mFormListener, e.toString());
        }
    }

    /**
     * This method is used for parsing the schema json and returns the form model object
     */
    private RFFormModel parseSchema() {

        RFFormModel form = new RFFormModel(new ArrayList<RFPageModel>());

        if (mSchemaJson == null) {
            return form;
        }

        ArrayList<String> errors = parseSchemaJson(mSchemaJson, form, 0, null, 0, null, null, 0, new ArrayList<String>());

        if (errors != null && errors.size() > 0) {
            // returning only first error.
            RFUtils.throwParseError(mFormListener, errors.get(0));
            form = null;
        }

        return form;
    }


    /**
     * This method is used for parsing the layout json and sets the layout to the corresponding form element(Form/page/section/element).
     * If the data in the json is invalid we should throw parse error.
     */
    private void parseLayout() {

        if (mForm == null) {
            return;
        }

        HashMap<String, JSONObject> layoutJsonObjects = getJsonData(mLayoutJson, mForm);

        if (layoutJsonObjects == null) {
            return;
        }

        ArrayList<String> errors = new ArrayList<>();
        Iterator layoutJsonObjectsIterator = layoutJsonObjects.entrySet().iterator();

        while (layoutJsonObjectsIterator.hasNext()) {

            Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>) layoutJsonObjectsIterator.next();
            JSONObject layoutJsonObject = pair.getValue();

            RFLayoutModel layout = null;
            JSONArray layoutProperties = layoutJsonObject.names();

            RFBaseModel modelObject = mFormElementModels.get(pair.getKey());

            // this means that the key doesn't belongs to form
            if (modelObject == null) {
                continue;
            }

            layout = modelObject.getLayout();

            for (int i = 0; i < layoutProperties.length(); i++) {
                try {
                    String propertyName = layoutProperties.getString(i);
                    switch (propertyName.toLowerCase()) {

                        // This will validates the data of the adjust columns property and sets to layout object otherwise if will add that to errors list.
                        case RFLayoutConstants.LC_ADJUST_COLUMNS:

                            if (RFJsonValidations.isValidBoolean(layoutJsonObject, propertyName)) {
                                boolean adjustColumns = Boolean.parseBoolean(layoutJsonObject.getString(propertyName).toLowerCase());
                                layout.setAdjustColumns(adjustColumns);
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.invalid_data_message) + pair.getKey() + " " + propertyName);
                            }

                            break;

                        // This will validates the data of the alignment property and sets to layout object otherwise if will add that to errors list.
                        case RFLayoutConstants.LC_ALIGNMENT:

                            String alignment = layoutJsonObject.getString(propertyName).trim();
                            if (RFLayoutValidations.isValidAlignment(alignment)) {
                                layout.setAlignment(alignment.toLowerCase());
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.invalid_data_message) + pair.getKey() + " " + propertyName);
                            }

                            break;

                        // This will validates the data of the padding property and sets to layout object otherwise if will add that to errors list.
                        case RFLayoutConstants.LC_PADDING:

                            String padding = layoutJsonObject.getString(propertyName).trim();
                            if (RFLayoutValidations.isValidPadding(padding)) {
                                try {

                                    JSONObject paddingJsonObject = new JSONObject(padding);
                                    JSONArray paddingTypes = paddingJsonObject.names();

                                    for (int paddingTypeCount = 0; paddingTypeCount < paddingTypes.length(); paddingTypeCount++) {

                                        String paddingType = paddingTypes.getString(paddingTypeCount).trim();
                                        float paddingValue = RFUtils.convertPixelsToDp(Float.parseFloat(paddingJsonObject.getString(paddingType)), mContext);

                                        switch (paddingType.toLowerCase()) {
                                            case RFLayoutConstants.LC_LEFT_PADDING:
                                                layout.setLeftPadding(paddingValue);
                                                break;
                                            case RFLayoutConstants.LC_RIGHT_PADDING:
                                                layout.setRightPadding(paddingValue);
                                                break;
                                            case RFLayoutConstants.LC_TOP_PADDING:
                                                layout.setTopPadding(paddingValue);
                                                break;
                                            case RFLayoutConstants.LC_BOTTOM_PADDING:
                                                layout.setBottomPadding(paddingValue);
                                        }

                                    }
                                } catch (JSONException e) {
                                    errors.add(RFUtils.getString(mContext, R.string.invalid_data_message) + pair.getKey() + " " + propertyName);
                                }
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.invalid_data_message) + pair.getKey() + " " + propertyName);
                            }

                            break;

                    }

                } catch (JSONException e) {
                    Log.e(TAG + " parseLayoutJson", e.toString());
                }
            }
        }

        // If there are errors in the layout json. It will return the first error to the user.
        if (errors.size() > 0) {
            // returning the first error.
            RFUtils.throwParseError(mFormListener, errors.get(0));
            mForm = null;
        }
    }

    /***
     * This method is used for parsing the style json and sets the style to the corresponding form element(Form/page/section/element).
     * If the data in the json is invalid we should throw parse error.
     */
    private void parseStyle() {

        if (mForm == null) {
            return;
        }

        HashMap<String, JSONObject> unOrderedStylingJsonObjects = getJsonData(mStyleJson, mForm);
        if (unOrderedStylingJsonObjects == null) {
            return;
        }

        LinkedHashMap<String, JSONObject> stylingJsonObjects = orderTheObjectsBasedOnHierarchy(unOrderedStylingJsonObjects);

        ArrayList<String> errors = new ArrayList<>();
        Iterator stylingJsonObjectsIterator = stylingJsonObjects.entrySet().iterator();

        while (stylingJsonObjectsIterator.hasNext()) {
            Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>) stylingJsonObjectsIterator.next();
            JSONObject stylingJsonObject = pair.getValue();
            JSONArray properties = stylingJsonObject.names();
            String keyName = pair.getKey();

            RFBaseModel modelObject = mFormElementModels.get(pair.getKey());

            if (modelObject == null) {
                continue;
            }

            RFStyleModel style = modelObject.getStyle();

            for (int i = 0; i < properties.length(); i++) {

                try {
                    String property = properties.getString(i);

                    Object propertyJson = stylingJsonObject.get(property);
                    switch (property.toLowerCase()) {

                        // This will validates the data of the background property and sets to style object otherwise if will add that to errors list.
                        case RFStyleConstants.SC_BACKGROUND:

                            if (RFStylingValidations.isValidBackground(propertyJson)) {
                                JSONObject backgroundJson = (JSONObject) propertyJson;
                                JSONArray backgroundProperties = backgroundJson.names();

                                for (int propertyCount = 0; propertyCount < backgroundProperties.length(); propertyCount++) {
                                    String backgroundProperty = backgroundProperties.getString(propertyCount);

                                    switch (backgroundProperty.toLowerCase()) {
                                        case RFStyleConstants.SC_BACKGROUND_COLOR:
                                            style.setBackgroundColor(backgroundJson.getString(backgroundProperty));
                                            break;
                                        case RFStyleConstants.SC_BACKGROUND_IMAGE:
                                            break;
                                        case RFStyleConstants.SC_BACKGROUND_MODE:
                                            break;
                                        case RFStyleConstants.SC_BORDER_COLOR:
                                            style.setBorderColor(backgroundJson.getString(backgroundProperty));
                                            break;
                                        case RFStyleConstants.SC_BORDER_THICKNESS:
                                            if (RFLayoutValidations.isValidFloat(backgroundJson.getString(backgroundProperty))) {
                                                float thickness = Float.parseFloat(backgroundJson.getString(backgroundProperty));
                                                style.setBorderThickness(RFUtils.convertPixelsToDp(thickness, mContext));
                                            }
                                            break;
                                        case RFStyleConstants.SC_CORNER_RADIUS:
                                            if (RFLayoutValidations.isValidFloat(backgroundJson.getString(backgroundProperty))) {
                                                float cornerRadius = Float.parseFloat(backgroundJson.getString(backgroundProperty));
                                                style.setCornerRadius(RFUtils.convertPixelsToDp(cornerRadius, mContext));
                                            }
                                            break;
                                    }
                                }

                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.background_error_message) + " " + keyName);
                            }
                            break;

                        // This will validates the data of the font property and sets to style object otherwise if will add that to errors list.
                        case RFStyleConstants.SC_FONT:
                            if (RFStylingValidations.isValidFont(propertyJson)) {

                                JSONObject fontJson = (JSONObject) propertyJson;
                                JSONArray fontProperties = fontJson.names();

                                for (int propertyCount = 0; propertyCount < fontProperties.length(); propertyCount++) {
                                    String fontProperty = fontProperties.getString(propertyCount);

                                    switch (fontProperty.toLowerCase()) {
                                        case RFStyleConstants.SC_FONT_COLOR:
                                            style.setFontColor(fontJson.getString(fontProperty));
                                            break;
                                        case RFStyleConstants.SC_FONT_SIZE:
                                            style.setFontSize(Float.parseFloat(fontJson.getString(fontProperty)));
                                            break;
                                        case RFStyleConstants.SC_FONT_STYLE:
                                            style.setFontStyle(fontProperty.toLowerCase());
                                            break;
                                        case RFStyleConstants.SC_FONT_NAME:
                                            style.setFontName(fontJson.getString(fontProperty));
                                            break;

                                    }
                                }
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.font_error_message) + " " + keyName);
                            }
                            break;

                        // This will validates the data of the title font property and sets to style object otherwise if will add that to errors list.
                        case RFStyleConstants.SC_TITLE_FONT:
                            if (RFStylingValidations.isValidFont(propertyJson)) {

                                JSONObject fontJson = (JSONObject) propertyJson;
                                JSONArray fontProperties = fontJson.names();

                                for (int propertyCount = 0; propertyCount < fontProperties.length(); propertyCount++) {
                                    String fontProperty = fontProperties.getString(propertyCount);

                                    switch (fontProperty.toLowerCase()) {
                                        case RFStyleConstants.SC_TITLE_FONT_COLOR:
                                            style.setTitleFontColor(fontJson.getString(fontProperty));
                                            break;
                                        case RFStyleConstants.SC_TITLE_FONT_SIZE:
                                            style.setTitleFontSize(Float.parseFloat(fontJson.getString(fontProperty)));
                                            break;
                                        case RFStyleConstants.SC_TITLE_FONT_STYLE:
                                            style.setTitleFontStyle(fontJson.getString(fontProperty));
                                            break;
                                    }
                                }
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.font_error_message) + " " + keyName);
                            }
                            break;

                        // This will validates the data of the visible property and sets to style object otherwise if will add that to errors list.
                        case RFStyleConstants.SC_VISIBILITY:
                            if (RFJsonValidations.isValidBoolean(stylingJsonObject, property)) {
                                style.setVisibility(Boolean.parseBoolean(propertyJson.toString().toLowerCase()));
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.visibility_error_message) + " " + keyName);
                            }
                            break;
                        case RFStyleConstants.SC_EDITABILITY:
                            if (RFJsonValidations.isValidBoolean(stylingJsonObject, property)) {
                                style.setEditability(Boolean.parseBoolean(propertyJson.toString().toLowerCase()));
                            } else {
                                errors.add(RFUtils.getString(mContext, R.string.editability_error_message) + " " + keyName);
                            }
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG + " parseStylingJson", e.toString());
                }

            }

            if (RFUtils.isInstance(RFFormModel.class, modelObject)) {
                applyFormStyleToPage((RFFormModel) modelObject, style);
            } else if (RFUtils.isInstance(RFPageModel.class, modelObject)) {
                applyPageStyleToSection((RFPageModel) modelObject, style);
            } else if (RFUtils.isInstance(RFSectionModel.class, modelObject)) {
                applySectionStyleToField((RFSectionModel) modelObject, style);
            }
        }

        // If there are errors in the layout json. It will return the first error to the user.
        if (errors.size() > 0) {

            // returning the first error.
            RFUtils.throwParseError(mFormListener, errors.get(0));
            mForm = null;
        }

    }

    /***
     * This method is used for parsing the validation json and sets the validation to the corresponding form element(Form/page/section/element).
     * If the data in the json is invalid we should throw parse error.
     */
    private void parseValidation() {

        if (mForm == null) {
            return;
        }

        HashMap<String, JSONObject> validationJsonObjects = getJsonData(mValidationJson, mForm);
        Log.e(TAG, "parseValidation: validationJsonObject ::" + validationJsonObjects);
        if (validationJsonObjects == null) {
            return;
        }



        ArrayList<String> errors = new ArrayList<>();
        Iterator validationJsonObjectsIterator = validationJsonObjects.entrySet().iterator();

        while (validationJsonObjectsIterator.hasNext()) {
            Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>) validationJsonObjectsIterator.next();
            Log.e(TAG, "parseValidation: pair ::" + pair);
            JSONObject validationJsonObject = pair.getValue();
            Log.e(TAG, "parseValidation: validationJsonObject ::" + validationJsonObject);
            JSONArray keys = validationJsonObject.names();
            if (keys != null) {
            RFValidationModel validationObject = new RFValidationModel();

            String methodName = "";
            String function = "";
            Log.e(TAG, "parseValidation: keys ::" + keys);


                for (int i = 0; i < keys.length(); i++) {
                    try {
                        String property = keys.getString(i);

                        // This will check whether the json contains the method, function or types of validations.
                        if (property.toLowerCase().equals(RFValidationConstants.VC_ON_FOCUS) || property.toLowerCase().equals(RFValidationConstants.VC_ON_FOCUS_LOST) || property.toLowerCase().equals(RFValidationConstants.VC_ON_CHANGE)) {
                            JSONObject methodJsonObject = validationJsonObject.getJSONObject(property);

                            JSONArray methodJsonObjectKeys = methodJsonObject.names();
                            for (int k = 0; k < methodJsonObjectKeys.length(); k++) {
                                String methodPropertyName = methodJsonObjectKeys.get(k).toString();
                                switch (methodPropertyName.toLowerCase()) {
                                    case RFValidationConstants.VC_METHOD_NAME:
                                        methodName = methodJsonObject.getString(RFValidationConstants.VC_METHOD_NAME);
                                        break;
                                    case RFValidationConstants.VC_FUNCTION:
                                        function = methodJsonObject.getString(RFValidationConstants.VC_FUNCTION);
                                        break;
                                }
                            }
                            if (methodName.length() == 0) {
                                errors.add(RFUtils.getString(mContext, R.string.method_name) + pair.getKey());
                            }

                            if (function.length() == 0) {
                                errors.add(RFUtils.getString(mContext, R.string.function) + pair.getKey());
                            }
                        }

                        // This will validates and set the method name and function to corresponding validation type.
                        switch (property.toLowerCase()) {
                            case RFValidationConstants.VC_ON_FOCUS:
                                validationObject.setOnFocus(new HashMap<String, String>());
                                validationObject.getOnFocus().put(methodName, function);
                                methodName = "";
                                function = "";
                                break;
                            case RFValidationConstants.VC_ON_FOCUS_LOST:
                                validationObject.setOnFocusLost(new HashMap<String, String>());
                                validationObject.getOnFocusLost().put(methodName, function);
                                methodName = "";
                                function = "";
                                break;
                            case RFValidationConstants.VC_ON_CHANGE:
                                validationObject.setOnValueChange(new HashMap<String, String>());
                                validationObject.getOnValueChange().put(methodName, function);
                                methodName = "";
                                function = "";
                                break;
                            case RFValidationConstants.VC_REQUIRED:
                                if (validationJsonObject.getBoolean(RFValidationConstants.VC_REQUIRED)) {
                                    validationObject.setRequired(true);
                                } else if (!validationJsonObject.getBoolean(RFValidationConstants.VC_REQUIRED)) {
                                    validationObject.setRequired(false);
                                } else {
                                    errors.add(RFUtils.getString(mContext, R.string.required_error_message) + pair.getKey());
                                }
                                break;
                            case RFValidationConstants.VC_METHOD_NAME:
                                methodName = validationJsonObject.getString(RFValidationConstants.VC_METHOD_NAME);
                                break;
                            case RFValidationConstants.VC_FUNCTION:
                                function = validationJsonObject.getString(RFValidationConstants.VC_FUNCTION);
                                break;
                        }

                    } catch (Exception e) {
                        Log.e(TAG + " parseValidationJson", e.toString());
                    }

                }


                // This will sets the data to the default validation type. ie. focus lost.
                if (methodName.length() != 0 || function.length() != 0) {
                    validationObject.setOnFocusLost(new HashMap<String, String>());
                    validationObject.getOnFocusLost().put(methodName, function);
                }

                // This will returns error if any validation type misses method or function.
                if (validationObject.getOnFocusLost() != null && (validationObject.getOnFocusLost().get(RFValidationConstants.VC_METHOD_NAME) != null && validationObject.getOnFocusLost().get(RFValidationConstants.VC_METHOD_NAME).length() == 0)
                        && (validationObject.getOnFocusLost().get(RFValidationConstants.VC_FUNCTION) != null && validationObject.getOnFocusLost().get(RFValidationConstants.VC_FUNCTION).length() != 0)) {
                    errors.add(RFUtils.getString(mContext, R.string.method_name) + pair.getKey());
                } else if (validationObject.getOnFocusLost() != null && (validationObject.getOnFocusLost().get(RFValidationConstants.VC_METHOD_NAME) != null && validationObject.getOnFocusLost().get(RFValidationConstants.VC_METHOD_NAME).length() != 0)
                        && (validationObject.getOnFocusLost().get(RFValidationConstants.VC_FUNCTION) != null && validationObject.getOnFocusLost().get(RFValidationConstants.VC_FUNCTION).length() == 0)) {
                    errors.add(RFUtils.getString(mContext, R.string.function) + pair.getKey());
                }

                // This will check whether this particular validation data is related to field or not. If it is is field then the validation is set to that field.
                RFBaseModel modelObject = mFormElementModels.get(pair.getKey());
                if (modelObject != null) {
                    modelObject.setValidation(validationObject);
                }

                if (errors.size() > 0) {
                    // returning only first error.
                    RFUtils.throwParseError(mFormListener, errors.get(0));
                    mForm = null;
                }
            }else{
                return;

            }
        }


    }

    /**
     * This method will parse the behaviour json.
     * If the data in the json is invalid we should throw parse error.
     */
    public void parseBehaviour() {
        if (mForm == null) {
            return;
        }

        HashMap<String, JSONObject> behaviourJsonObjects = getJsonData(mBehaviourJson, mForm);

        if (behaviourJsonObjects == null) {
            return;
        }

        ArrayList<String> errors = new ArrayList<>();
        Iterator behaviourJsonObjectsIterator = behaviourJsonObjects.entrySet().iterator();

        while (behaviourJsonObjectsIterator.hasNext()) {
            Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>) behaviourJsonObjectsIterator.next();
            String behaviourElementKey = pair.getKey();
            JSONObject behaviourJsonObject = pair.getValue();

            RFBaseModel modelObject = mFormElementModels.get(behaviourElementKey);
            HashMap<String, HashMap<RFBaseModel, RFBehaviourModel>> behaviour = null;

            if (modelObject == null) {
                continue;
            }

            behaviour = modelObject.getBehaviour();

            JSONArray behaviours = behaviourJsonObject.names();

            for (int noOfBehaviours = 0; noOfBehaviours < behaviours.length(); noOfBehaviours++) {

                try {

                    HashMap<RFBaseModel, RFBehaviourModel> behaviourHashMap = new HashMap<RFBaseModel, RFBehaviourModel>();
                    behaviour.put(behaviours.getString(noOfBehaviours), behaviourHashMap);

                    if (RFJsonValidations.isJSON(behaviourJsonObject.get(behaviours.getString(noOfBehaviours)))) {
                        JSONObject currentBehaviour = behaviourJsonObject.getJSONObject(behaviours.getString(noOfBehaviours));
                        JSONArray keys = currentBehaviour.names();

                        for (int i = 0; i < keys.length(); i++) {
                            String property = keys.get(i).toString();

                            switch (property.toLowerCase()) {
                                case RFValidationConstants.VC_VISIBILITY:
                                    if (RFJsonValidations.isJSON(currentBehaviour.get(RFValidationConstants.VC_VISIBILITY))) {
                                        JSONObject visibilityJson = currentBehaviour.getJSONObject(RFValidationConstants.VC_VISIBILITY);
                                        JSONArray visibilityJsonKeys = visibilityJson.names();

                                        for (int j = 0; j < visibilityJsonKeys.length(); j++) {

                                            RFBehaviourModel behaviourModel = null;
                                            String elementKey = visibilityJsonKeys.getString(j);
                                            RFBaseModel dependentModelObject = mFormElementModels.get(elementKey);

                                            if (dependentModelObject == null) {
                                                continue;
                                            }

                                            if (behaviourHashMap.containsKey(dependentModelObject)) {
                                                behaviourModel = behaviourHashMap.get(dependentModelObject);
                                            } else {
                                                behaviourModel = new RFBehaviourModel();
                                                behaviourHashMap.put(dependentModelObject, behaviourModel);
                                            }

                                            if (RFJsonValidations.isValidBoolean(visibilityJson, elementKey)) {
                                                behaviourModel.setVisibility(Boolean.parseBoolean(visibilityJson.getString(elementKey)));
                                            } else {
                                                errors.add(RFUtils.getString(mContext, R.string.visibility_error_message) + " " + elementKey);
                                            }
                                        }

                                    } else {
                                        errors.add(RFUtils.getString(mContext, R.string.visibility_json_error_message) + pair.getKey());
                                    }
                                    break;
                                case RFValidationConstants.VC_EDITABILITY:
                                    if (RFJsonValidations.isJSON(currentBehaviour.get(RFValidationConstants.VC_EDITABILITY))) {
                                        JSONObject editabilityJson = currentBehaviour.getJSONObject(RFValidationConstants.VC_EDITABILITY);
                                        JSONArray editabilityJsonKeys = editabilityJson.names();

                                        for (int j = 0; j < editabilityJsonKeys.length(); j++) {

                                            RFBehaviourModel behaviourModel = null;
                                            String elementKey = editabilityJsonKeys.getString(j);
                                            RFBaseModel dependentModelObject = mFormElementModels.get(elementKey);

                                            if (dependentModelObject == null) {
                                                continue;
                                            }

                                            if (behaviourHashMap.containsKey(dependentModelObject)) {
                                                behaviourModel = behaviourHashMap.get(dependentModelObject);
                                            } else {
                                                behaviourModel = new RFBehaviourModel();
                                                behaviourHashMap.put(dependentModelObject, behaviourModel);
                                            }

                                            if (RFJsonValidations.isValidBoolean(editabilityJson, elementKey)) {
                                                behaviourModel.setEditability(Boolean.parseBoolean(editabilityJson.getString(elementKey)));
                                            } else {
                                                errors.add(RFUtils.getString(mContext, R.string.editability_error_message) + " " + elementKey);
                                            }
                                        }

                                    } else {
                                        errors.add(RFUtils.getString(mContext, R.string.editability_json_error_message) + pair.getKey());
                                    }
                                    break;
                                case RFValidationConstants.VC_DATA:
                                    if (RFJsonValidations.isJSON(currentBehaviour.get(RFValidationConstants.VC_DATA))) {
                                        JSONObject dataJson = currentBehaviour.getJSONObject(RFValidationConstants.VC_DATA);
                                        JSONArray editabilityJsonKeys = dataJson.names();

                                        for (int j = 0; j < editabilityJsonKeys.length(); j++) {

                                            RFBehaviourModel behaviourModel = null;
                                            String elementKey = editabilityJsonKeys.getString(j);
                                            RFBaseModel dependentModelObject = mFormElementModels.get(elementKey);

                                            if (dependentModelObject == null) {
                                                continue;
                                            }

                                            if (behaviourHashMap.containsKey(dependentModelObject)) {
                                                behaviourModel = behaviourHashMap.get(dependentModelObject);
                                            } else {
                                                behaviourModel = new RFBehaviourModel();
                                                behaviourHashMap.put(dependentModelObject, behaviourModel);
                                            }

                                            behaviourModel.setData(dataJson.getString(elementKey));
                                        }

                                    } else {
                                        errors.add(RFUtils.getString(mContext, R.string.editability_json_error_message) + pair.getKey());
                                    }
                                    break;

                            }
                        }

                    } else {
                        continue;
                    }

                } catch (JSONException e) {
                    Log.e(TAG + " parseBehaviour", e.toString());
                }
            }
        }

        if (errors.size() > 0) {
            // returning only first error.
            RFUtils.throwParseError(mFormListener, errors.get(0));
            mForm = null;
        }

    }

    /**
     * This will parses the form data and sets data to the corresponding data to models.
     *
     * @param formData
     */
    private void parseFormData(JSONObject formData) {

        if (mForm == null || formData == null) {
            return;
        }

        try {

            JSONObject errorJsonObject = (JSONObject) formData.get(RFConstants.FC_ERRORS);
            JSONObject dataJsonObject = (JSONObject) formData.get(RFConstants.FC_DATA);

            ArrayList<RFPageModel> pages = mForm.getPages();

            for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++) {

                RFPageModel currentPage = pages.get(pageNumber);

                ArrayList<RFSectionModel> sections = currentPage.getSections();

                for (int sectionNumber = 0; sectionNumber < sections.size(); sectionNumber++) {

                    RFSectionModel currentSection = sections.get(sectionNumber);

                    ArrayList<RFElementModel> fields = currentSection.getFields();

                    for (int fieldNumber = 0; fieldNumber < fields.size(); fieldNumber++) {

                        RFElementModel currentField = fields.get(fieldNumber);

                        if (!dataJsonObject.isNull(currentField.getId())) {
                            currentField.setValue(dataJsonObject.getString(currentField.getId()));
                        }

                        if (!errorJsonObject.isNull(currentField.getId())) {
                            currentField.setErrorMessage(errorJsonObject.getString(currentField.getId()));
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG + " parseFormData", e.toString());
        }
    }

    /**
     * This will parse the schema json and sets the data to the form. If the data in the json is invalid we should throw parse error.
     * If the specified type is invalid we should throw parse error.
     *
     * @param formElementsObject
     * @param form
     * @param pageNumber
     * @param pagesInForm
     * @param sectionNumber
     * @param sectionsInForm
     * @param formElements
     * @param depth
     * @param errors
     */

    /*
            depth = 0  - compiler is at form level
            depth = 1  - compiler is at page level
            depth = 2  - compiler is at section level
            depth = 3  - compiler is at field in a section

            In the depth 0 it will creates form object. If the json object at depth 0 is an array, it will starts recursion.
            In the depth 1 it will creates page object. If the json object is array it will starts recursion otherwise it creates section object and field object.
            In the depth 2 it will creates section object. If the json object is array it will starts recu other it creates dummy sections and fields to that section.
            In the depth 3 it will creates fields and add those fields to section.

            Note: If a field object is created it will be added to section.
            If a section object is created it will added to page
            If a page object is created it will be added to form.

            If a page contains a field directly, first it will search for default section. If default is not present it will create default section and add those fields to that section.
            The id of the default section contains "default_section"
     */
    private ArrayList<String> parseSchemaJson(Object formElementsObject, RFFormModel form, int pageNumber, ArrayList<RFPageModel> pagesInForm, int sectionNumber, ArrayList<RFSectionModel> sectionsInForm, ArrayList<RFElementModel> formElements, int depth, ArrayList<String> errors) {

        if (errors.size() > 0) {
            return errors;
        }

        if (RFJsonValidations.isJSON(formElementsObject)) {

            JSONObject jsonObject = (JSONObject) formElementsObject;
            JSONArray keys = jsonObject.names();

            try {

                String keyName = "";
                String title = "";
                String titleKeyName = "";

                if (keys != null && keys.length() > 0) {
                    for (int i = 0; i < keys.length(); i++) {
                        if (keys.get(i).equals(RFConstants.FC_META)) {

                            // TODO: Need to move that to high level parser
                            JSONObject metaJson = jsonObject.getJSONObject(RFConstants.FC_META);
                            title = metaJson.getString(RFConstants.FC_TITLE);
                        } else {
                            keyName = keys.getString(i);
                        }
                    }
                }

                if (mFormElementModels.containsKey(keyName)) {
                    errors.add(RFUtils.getString(mContext, R.string.duplicate_keys) + keyName);
                    return errors;
                }

                if (title.length() == 0) {
                    title = keyName;
                }

                Object object = jsonObject.get(keyName);
                if (RFJsonValidations.isJSONArray(object)) {

                    JSONArray jsonArray = (JSONArray) object;

                    if (depth == 0) {
                        form.setId(keyName);
                        form.setName(title);

                        mFormElementModels.put(keyName, form);

                        pagesInForm = form.getPages();
                    } else if (depth == 1) {
                        form.getPagePositionDetails().put(keyName, pagesInForm.size());

                        RFPageModel page = new RFPageModel(form, keyName, title, new ArrayList<RFSectionModel>());
                        sectionsInForm = page.getSections();
                        pagesInForm.add(page);

                        mFormElementModels.put(keyName, page);

                        String sectionKeyName = RFConstants.FC_DEFAULT_SECTION + sectionsInForm.size();
                        page.getSectionPositionDetails().put(sectionKeyName, sectionsInForm.size());

                        RFSectionModel section = new RFSectionModel(page, sectionKeyName, new ArrayList<RFElementModel>());
                        formElements = section.getFields();

                        sectionsInForm.add(section);


                    } else if (depth == 2) {
                        RFPageModel currentPage = pagesInForm.get(pageNumber);
                        currentPage.getSectionPositionDetails().put(keyName, sectionsInForm.size());

                        RFSectionModel section = new RFSectionModel(currentPage, keyName, title, new ArrayList<RFElementModel>());
                        formElements = section.getFields();
                        sectionsInForm.add(section);

                        mFormElementModels.put(keyName, section);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (depth == 1) {
                            parseSchemaJson(jsonArray.get(i), form, pagesInForm.size() - 1, pagesInForm, sectionNumber, sectionsInForm, formElements, depth + 1, errors);
                        } else if (depth == 0) {
                            parseSchemaJson(jsonArray.get(i), form, pagesInForm.size(), pagesInForm, sectionNumber, sectionsInForm, formElements, depth + 1, errors);
                        } else if (depth == 2) {
                            parseSchemaJson(jsonArray.get(i), form, pageNumber, pagesInForm, sectionsInForm.size() - 1, sectionsInForm, formElements, depth + 1, errors);
                        } else {
                            errors.add(RFUtils.getString(mContext, R.string.inner_section_error));
                        }
                    }

                } else {
                    RFPageModel page = null;
                    RFSectionModel section = null;
                    RFElementModel field = null;
                    if (depth == 0) {
                        errors.add(RFUtils.getString(mContext, R.string.invalid_schema));
                        return errors;
                    } else if (depth == 1) {
                        // All the key names should be unique. That is y here we are assigning GUID value to pageKeyName
                        String pageKeyName = UUID.randomUUID().toString();
                        form.getPagePositionDetails().put(pageKeyName, pagesInForm.size());

                        // Here we are not specifying page name because it is a dummy page
                        page = new RFPageModel(form, pageKeyName, "", new ArrayList<RFSectionModel>());
                        sectionsInForm = page.getSections();

                        pagesInForm.add(page);

                        section = getDefaultSection(page);

                        if (section == null) {
                            String sectionKeyName = RFConstants.FC_DEFAULT_SECTION + sectionsInForm.size();
                            page.getSectionPositionDetails().put(sectionKeyName, sectionsInForm.size());
                            section = new RFSectionModel(page, sectionKeyName, new ArrayList<RFElementModel>());
                            formElements = section.getFields();
                            sectionsInForm.add(section);
                        }

                        formElements = section.getFields();
                        section.getFieldPositionDetails().put(keyName, formElements.size());

                    } else if (depth == 2) {
                        page = pagesInForm.get(pageNumber);

                        section = getDefaultSection(page);

                        if (section == null) {
                            String sectionKeyName = RFConstants.FC_DEFAULT_SECTION + sectionsInForm.size();
                            page.getSectionPositionDetails().put(sectionKeyName, sectionsInForm.size());
                            section = new RFSectionModel(page, sectionKeyName, new ArrayList<RFElementModel>());
                            formElements = section.getFields();
                            sectionsInForm.add(section);
                        }

                        formElements = section.getFields();
                        section.getFieldPositionDetails().put(keyName, formElements.size());

                    } else if (depth == 3) {
                        section = sectionsInForm.get(sectionNumber);
                        section.getFieldPositionDetails().put(keyName, formElements.size());
                    }

                    Object fieldObject = jsonObject.get(keyName);
                    String type = "text";
                    String name = "";
                    LinkedHashMap<String, String> data = null;

                    if (RFJsonValidations.isJSON(fieldObject)) {
                        JSONObject fieldJsonObject = (JSONObject) fieldObject;
                        JSONArray fieldProperties = fieldJsonObject.names();

                        for (int i = 0; i < fieldProperties.length(); i++) {
                            String property = fieldProperties.getString(i);

                            switch (property.toLowerCase()) {
                                case RFConstants.FC_TYPE:
                                    type = fieldJsonObject.getString(property);
                                    if (!RFJsonValidations.isValidType(type, mRegister)) {
                                        errors.add(RFUtils.getString(mContext, R.string.invalid_type) + type);
                                        return errors;
                                    }
                                    break;
                                case RFConstants.FC_TITLE:
                                    name = fieldJsonObject.getString(property);
                                    break;
                                case RFConstants.FC_DATA:
                                    if (RFJsonValidations.isValidData(fieldJsonObject.get(property))) {
                                        data = getDataFromJson(fieldJsonObject.getJSONArray(property));
                                    }
                                    break;
                            }

                        }

                        if (name != null && name.trim().length() == 0) {
                            name = keyName;
                        }

                    } else {
                        name = keyName;
                        type = fieldObject.toString();
                        if (!RFJsonValidations.isValidType(type, mRegister)) {
                            errors.add(RFUtils.getString(mContext, R.string.invalid_type) + "1066" + type);
                            return errors;
                        }
                    }

                    RFElementModel element;

                    if (data == null) {
                        element = new RFElementModel(section, keyName, name, type);
                    } else {
                        element = new RFElementModel(section, keyName, name, type, data);
                    }

                    formElements.add(element);

                    mFormElementModels.put(keyName, element);
                }
            } catch (Exception e) {
                Log.e(TAG + " parseFormElementJson", e.toString());
            }

        } else {
            RFPageModel page = null;
            RFSectionModel section = null;

            if (mFormElementModels.containsKey(formElementsObject.toString())) {
                errors.add(RFUtils.getString(mContext, R.string.duplicate_keys) + formElementsObject.toString());
                return errors;
            }

            if (depth == 0) {
                errors.add(RFUtils.getString(mContext, R.string.invalid_schema));
                return errors;
            } else if (depth == 1) {
                // All the key names should be unique. That is y here we are assigning GUID value to pageKeyName
                String pageKeyName = UUID.randomUUID().toString();
                form.getPagePositionDetails().put(pageKeyName, pagesInForm.size());

                // Here we are not specifying page name because it is a dummy page
                page = new RFPageModel(form, pageKeyName, "", new ArrayList<RFSectionModel>());
                sectionsInForm = page.getSections();

                pagesInForm.add(page);

                section = getDefaultSection(page);

                if (section == null) {
                    String sectionKeyName = RFConstants.FC_DEFAULT_SECTION + sectionsInForm.size();
                    page.getSectionPositionDetails().put(sectionKeyName, sectionsInForm.size());
                    section = new RFSectionModel(page, sectionKeyName, new ArrayList<RFElementModel>());
                    formElements = section.getFields();
                    sectionsInForm.add(section);
                }

                formElements = section.getFields();
                section.getFieldPositionDetails().put(formElementsObject.toString(), formElements.size());

            } else if (depth == 2) {
                page = pagesInForm.get(pageNumber);

                section = getDefaultSection(page);

                if (section == null) {
                    String sectionKeyName = RFConstants.FC_DEFAULT_SECTION + sectionsInForm.size();
                    page.getSectionPositionDetails().put(sectionKeyName, sectionsInForm.size());
                    section = new RFSectionModel(page, sectionKeyName, new ArrayList<RFElementModel>());
                    formElements = section.getFields();
                    sectionsInForm.add(section);
                }

                formElements = section.getFields();
                section.getFieldPositionDetails().put(formElementsObject.toString(), formElements.size());

            } else if (depth == 3) {
                section = sectionsInForm.get(sectionNumber);
                section.getFieldPositionDetails().put(formElementsObject.toString(), formElements.size());
            }

            String name = formElementsObject.toString();
            String type = RFElementTypeConstants.ETC_TEXT;

            //here the onKeyGeneration name and label of view both are same
            RFElementModel element = new RFElementModel(section, name, name, type);
            formElements.add(element);

            mFormElementModels.put(name, element);
        }
        return errors;
    }

    /**
     * Use: retrieves the field/element data from the form's json
     *
     * @param array data array
     * @return
     */
    private LinkedHashMap<String, String> getDataFromJson(JSONArray array) {
        if (array == null || array.length() == 0) {
            return null;
        }
        LinkedHashMap<String, String> data = new LinkedHashMap<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject option = array.getJSONObject(i);
                JSONArray keys = option.names();
                if (keys != null) {
                    String key = keys.getString(0);
                    data.put(key, option.getString(key));
                }

            } catch (Exception e) {
                Log.e(TAG + " getDataFromJson", e.toString());
            }

        }
        return data;
    }

    /**
     * This will parse the json and replaces the references with json object and also validates the json and calls parse error if any
     *
     * @param jsonObject here the JSONObject can be a layout json, styling json and validation json.
     * @return returns the hash map with the key name and corresponding inner data json.
     */
    private HashMap<String, JSONObject> getJsonData(JSONObject jsonObject, RFFormModel form) {

        if (jsonObject == null) {
            return null;
        }

        HashMap<String, JSONObject> hashMap = new HashMap<>();
        ArrayList<String> haveToVerifyLayoutExistence = new ArrayList<>();
        ArrayList<String> haveToVerifyLayoutExistenceKey = new ArrayList<>();
        ArrayList<String> errors = new ArrayList<>();

        JSONArray names = jsonObject.names();

        if (names == null) {
            return null;
        }

        for (int i = 0; i < names.length(); i++) {
            try {
                JSONObject dataJsonObject = jsonObject.getJSONObject(names.getString(i));
                hashMap.put(names.getString(i), dataJsonObject);
                continue;
            } catch (JSONException e) {
            }

            try {
                haveToVerifyLayoutExistence.add(jsonObject.getString(names.getString(i)));
                haveToVerifyLayoutExistenceKey.add(names.getString(i));
            } catch (JSONException e1) {
                Log.e(TAG + " validateJson", e1.toString());
            }
        }

        // This will verify whether the reference is existed or not. If the reference is not existed it will add that to the error.
        for (int i = 0; i < haveToVerifyLayoutExistence.size(); i++) {
            if (hashMap.get(haveToVerifyLayoutExistence.get(i)) == null) {
                errors.add(RFUtils.getString(mContext, R.string.json_error_message) + haveToVerifyLayoutExistence.get(i));
            } else {
                hashMap.put(haveToVerifyLayoutExistenceKey.get(i), hashMap.get(haveToVerifyLayoutExistence.get(i)));
            }
        }

        if (errors.size() > 0) {

            // returning only first error.
            RFUtils.throwParseError(mFormListener, errors.get(0));
            form = null;
            return null;
        }
        return hashMap;
    }

    /**
     * This method will return page if the form contain any page with that key name.
     *
     * @param form
     * @param keyName
     * @return
     */
    private RFPageModel getPage(RFFormModel form, String keyName) {

        if (form.getPagePositionDetails().get(keyName) != null) {
            int position = form.getPagePositionDetails().get(keyName);
            return form.getPages().get(position);
        }

        return null;
    }

    /**
     * This method will return section if the form contain any section with that key name.
     *
     * @param form
     * @param keyName
     * @return
     */
    private RFSectionModel getSection(RFFormModel form, String keyName) {

        ArrayList<RFPageModel> pagesList = form.getPages();

        for (int i = 0; i < pagesList.size(); i++) {
            RFPageModel page = pagesList.get(i);
            if (page.getSectionPositionDetails().get(keyName) != null) {
                int position = page.getSectionPositionDetails().get(keyName);
                return page.getSections().get(position);
            }
        }
        return null;
    }

    /**
     * This method will return field if the form contain any field with that key name.
     *
     * @param form
     * @param keyName
     * @return
     */
    private RFElementModel getField(RFFormModel form, String keyName) {

        ArrayList<RFPageModel> pagesList = form.getPages();

        for (int i = 0; i < pagesList.size(); i++) {

            RFPageModel page = pagesList.get(i);
            ArrayList<RFSectionModel> sectionsList = page.getSections();

            for (int j = 0; j < sectionsList.size(); j++) {
                RFSectionModel section = sectionsList.get(j);

                if (section.getFieldPositionDetails().get(keyName) != null) {
                    int position = section.getFieldPositionDetails().get(keyName);
                    return section.getFields().get(position);
                }
            }
        }

        return null;
    }

    /**
     * Use: this will return default section
     *
     * @param pageModel
     * @return
     */
    private RFSectionModel getDefaultSection(RFPageModel pageModel) {

        ArrayList<RFSectionModel> sections = pageModel.getSections();

        if (sections == null || sections.size() == 0) {
            return null;
        }

        // This will check whether the last section is default or not. If it is not default it will create new default section otherwise returns the last section.
        RFSectionModel lastSection = sections.get(sections.size() - 1);
        if (lastSection.getId().contains(RFConstants.FC_DEFAULT_SECTION)) {
            return lastSection;
        }
        return null;
    }

    /**
     * This method will ordered the list according to their hierarchy
     *
     * @param unOrderedList
     * @return
     */
    private LinkedHashMap<String, JSONObject> orderTheObjectsBasedOnHierarchy(HashMap<String, JSONObject> unOrderedList) {
        LinkedHashMap<String, JSONObject> orderedList = new LinkedHashMap<>();
        LinkedHashMap<String, JSONObject> pagesList = new LinkedHashMap<>();
        LinkedHashMap<String, JSONObject> sectionsList = new LinkedHashMap<>();
        LinkedHashMap<String, JSONObject> fieldsList = new LinkedHashMap<>();

        Iterator unOrderedListIterator = unOrderedList.entrySet().iterator();

        while (unOrderedListIterator.hasNext()) {
            Map.Entry<String, JSONObject> current = (Map.Entry<String, JSONObject>) unOrderedListIterator.next();

            String key = current.getKey();
            JSONObject jsonObject = current.getValue();

            RFBaseModel model = mFormElementModels.get(key);

            if (RFUtils.isInstance(RFFormModel.class, model)) {
                orderedList.put(key, jsonObject);
            } else if (RFUtils.isInstance(RFPageModel.class, model)) {
                pagesList.put(key, jsonObject);
            } else if (RFUtils.isInstance(RFSectionModel.class, model)) {
                sectionsList.put(key, jsonObject);
            } else if (RFUtils.isInstance(RFElementModel.class, model)) {
                fieldsList.put(key, jsonObject);
            }
        }

        orderedList.putAll(pagesList);
        orderedList.putAll(sectionsList);
        orderedList.putAll(fieldsList);

        return orderedList;
    }

    /**
     * This method will appy the style of the form to page.
     *
     * @param formModel
     * @param styleModel
     */
    private void applyFormStyleToPage(RFFormModel formModel, RFStyleModel styleModel) {
        int numberOfPages = formModel.getPages().size();

        for (int i = 0; i < numberOfPages; i++) {

            RFPageModel pageModel = formModel.getPages().get(i);
            RFStyleModel pageStyleModel = pageModel.getStyle();

            pageStyleModel.setEditability(styleModel.isEditability());
            pageStyleModel.setVisibility(styleModel.isVisibility());

            pageStyleModel.setFontName(styleModel.getFontName());

            pageStyleModel.setFontColor(styleModel.getFontColor());
            pageStyleModel.setFontSize(styleModel.getFontSize());
            pageStyleModel.setFontStyle(styleModel.getFontStyle());

            pageStyleModel.setTitleFontColor(styleModel.getTitleFontColor());
            pageStyleModel.setTitleFontSize(styleModel.getTitleFontSize());
            pageStyleModel.setTitleFontStyle(styleModel.getTitleFontStyle());


            applyPageStyleToSection(pageModel, styleModel);
        }
    }

    /**
     * This method will appy the style of the page to section.
     *
     * @param pageModel
     * @param styleModel
     */
    private void applyPageStyleToSection(RFPageModel pageModel, RFStyleModel styleModel) {
        int numberOfSections = pageModel.getSections().size();

        for (int i = 0; i < numberOfSections; i++) {

            RFSectionModel sectionModel = pageModel.getSections().get(i);
            RFStyleModel sectionStyleModel = sectionModel.getStyle();

            sectionStyleModel.setEditability(styleModel.isEditability());
            sectionStyleModel.setVisibility(styleModel.isVisibility());

            sectionStyleModel.setFontName(styleModel.getFontName());

            sectionStyleModel.setFontColor(styleModel.getFontColor());
            sectionStyleModel.setFontSize(styleModel.getFontSize());
            sectionStyleModel.setFontStyle(styleModel.getFontStyle());

            sectionStyleModel.setTitleFontColor(styleModel.getTitleFontColor());
            sectionStyleModel.setTitleFontSize(styleModel.getTitleFontSize());
            sectionStyleModel.setTitleFontStyle(styleModel.getTitleFontStyle());


            applySectionStyleToField(sectionModel, styleModel);
        }
    }

    /**
     * This method will appy the style of the section to field.
     *
     * @param sectionModel
     * @param styleModel
     */
    private void applySectionStyleToField(RFSectionModel sectionModel, RFStyleModel styleModel) {
        int numberOfFields = sectionModel.getFields().size();

        for (int i = 0; i < numberOfFields; i++) {

            RFElementModel fieldModel = sectionModel.getFields().get(i);
            RFStyleModel fieldStyleModel = fieldModel.getStyle();

            fieldStyleModel.setEditability(styleModel.isEditability());
            fieldStyleModel.setVisibility(styleModel.isVisibility());

            fieldStyleModel.setFontName(styleModel.getFontName());

            fieldStyleModel.setFontColor(styleModel.getFontColor());
            fieldStyleModel.setFontSize(styleModel.getFontSize());
            fieldStyleModel.setFontStyle(styleModel.getFontStyle());

            fieldStyleModel.setTitleFontColor(styleModel.getTitleFontColor());
            fieldStyleModel.setTitleFontSize(styleModel.getTitleFontSize());
            fieldStyleModel.setTitleFontStyle(styleModel.getTitleFontStyle());
        }
    }

}
