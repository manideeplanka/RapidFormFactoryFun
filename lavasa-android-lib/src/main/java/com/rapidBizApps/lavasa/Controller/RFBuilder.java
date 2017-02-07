package com.rapidBizApps.lavasa.Controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.rapidBizApps.lavasa.Exceptions.RFInvalidFormView;
import com.rapidBizApps.lavasa.Exceptions.RFInvalidKeyName;
import com.rapidBizApps.lavasa.Exceptions.RFNullContextException;
import com.rapidBizApps.lavasa.Exceptions.RFNullFormException;
import com.rapidBizApps.lavasa.Exceptions.RFRegisterException;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;
import com.rapidBizApps.lavasa.Models.RFPagesStates;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cdara on 09-12-2015.
 */

/**
 * Use: User can access the  methods in the RFBuilder for form generation.
 */
public final class RFBuilder {

    private Context mContext;
    private RFController mController;

    // this boolean is for stopping generateForm more than once
    private boolean isFormGenerated;

    /**
     * This method will take the json in the form of string and renders the form.
     *
     * @param form         Form Json in string format.
     * @param formView
     * @param formListener
     */
    public void generateFormWithString(String form, View formView, RFFormListener formListener) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(form, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }
        isFormGenerated = true;
        mController.generateForm(RFController.FileType.STRING, form, null, formView, formListener);
    }

    /**
     * This method will take the json in the form of string and renders the form.
     * This method will prepopulate the field with the formData.
     *
     * @param form
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithString(String form, View formView, RFFormListener formListener, String formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(form, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.STRING, form, formData, formView, formListener);
    }

    /**
     * This method will take the json in the form of string and renders the form.
     * This method will prepopulate the field with the formData
     *
     * @param form
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithString(String form, View formView, RFFormListener formListener, JSONObject formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(form, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        if (formData != null) {
            mController.generateForm(RFController.FileType.STRING, form, formData.toString(), formView, formListener);
        } else {
            mController.generateForm(RFController.FileType.STRING, form, null, formView, formListener);
        }
    }

    /**
     * This method will take the json in the form of string and renders the form.
     *
     * @param form         Form Json in string format.
     * @param formView
     * @param formListener
     */
    public void generateFormWithString(String form, View formView, RFFormListener formListener, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithString(form, formView, formListener);
    }

    /**
     * This method will take the json in the form of string and renders the form.
     * This method will prepopulate the field with the formData.
     *
     * @param form
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithString(String form, View formView, RFFormListener formListener, String formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithString(form, formView, formListener, formData);
    }

    /**
     * This method will take the json in the form of string and renders the form.
     * This method will prepopulate the field with the formData
     *
     * @param form
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithString(String form, View formView, RFFormListener formListener, JSONObject formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithString(form, formView, formListener, formData);
    }

    /**
     * This method will take  file path which contain the form json.
     *
     * @param filePath
     * @param formView
     * @param formListener
     */
    public void generateFormWithFilepath(String filePath, View formView, RFFormListener formListener) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(filePath, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.FILEPATH, filePath, null, formView, formListener);
    }

    /**
     * This method will take  file path which contain the form json.
     * This method will prepopulate the field with the formData
     *
     * @param filePath
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFilepath(String filePath, View formView, RFFormListener formListener, String formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(filePath, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.FILEPATH, filePath, formData, formView, formListener);
    }

    /**
     * This method will take file path which contain the form json.
     * This method will prepopulate the field with the formData.
     *
     * @param filePath
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFilepath(String filePath, View formView, RFFormListener formListener, JSONObject formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(filePath, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        if (formData != null) {
            mController.generateForm(RFController.FileType.FILEPATH, filePath, formData.toString(), formView, formListener);
        } else {
            mController.generateForm(RFController.FileType.FILEPATH, filePath, null, formView, formListener);
        }
    }

    /**
     * This method will take  file path which contain the form json.
     *
     * @param filePath
     * @param formView
     * @param formListener
     */
    public void generateFormWithFilepath(String filePath, View formView, RFFormListener formListener, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithFilepath(filePath, formView, formListener);
    }

    /**
     * This method will take  file path which contain the form json.
     * This method will prepopulate the field with the formData
     *
     * @param filePath
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFilepath(String filePath, View formView, RFFormListener formListener, String formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithFilepath(filePath, formView, formListener, formData);
    }

    /**
     * This method will take file path which contain the form json.
     * This method will prepopulate the field with the formData.
     *
     * @param filePath
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFilepath(String filePath, View formView, RFFormListener formListener, JSONObject formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithFilepath(filePath, formView, formListener, formData);
    }


    /**
     * This method will take  assets file name which contain the form json.
     *
     * @param fileName
     * @param formView
     * @param formListener
     */
    public void generateFormWithFileName(String fileName, View formView, RFFormListener formListener) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(fileName, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.FILE_NAME, fileName, null, formView, formListener);
    }

    /**
     * This method will take  assets file name which contain the form json.
     * This method will prepopulate the field with the formData
     *
     * @param fileName
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFileName(String fileName, View formView, RFFormListener formListener, String formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(fileName, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.FILE_NAME, fileName, formData, formView, formListener);
    }

    /**
     * This method will take  assets file name which contain the form json.
     * This method will prepopulate the field with the formData
     *
     * @param fileName
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFileName(String fileName, View formView, RFFormListener formListener, JSONObject formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(fileName, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        if (formData != null) {
            mController.generateForm(RFController.FileType.FILE_NAME, fileName, formData.toString(), formView, formListener);
        } else {
            mController.generateForm(RFController.FileType.FILE_NAME, fileName, null, formView, formListener);
        }
    }

    /**
     * This method will take  assets file name which contain the form json.
     *
     * @param fileName
     * @param formView
     * @param formListener
     */
    public void generateFormWithFileName(String fileName, View formView, RFFormListener formListener, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithFileName(fileName, formView, formListener);
    }

    /**
     * This method will take  assets file name which contain the form json.
     * This method will prepopulate the field with the formData
     *
     * @param filePath
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFileName(String filePath, View formView, RFFormListener formListener, String formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithFileName(filePath, formView, formListener, formData);
    }

    /**
     * This method will take  assets file name which contain the form json.
     * This method will prepopulate the field with the formData
     *
     * @param filePath
     * @param formData     - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithFileName(String filePath, View formView, RFFormListener formListener, JSONObject formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithFileName(filePath, formView, formListener, formData);
    }

    /**
     * This method will take the form json and renders the form.
     *
     * @param formJsonObject
     * @param formView
     * @param formListener
     */
    public void generateFormWithJsonObject(JSONObject formJsonObject, View formView, RFFormListener formListener) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(formJsonObject, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.JSON_OBJECT, formJsonObject, null, formView, formListener);
    }

    /**
     * This method will take the form json and renders the form.
     * This method will prepopulate the field with the formData.
     *
     * @param formJsonObject
     * @param formData       - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithJsonObject(JSONObject formJsonObject, View formView, RFFormListener formListener, JSONObject formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(formJsonObject, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        if (formData != null) {
            mController.generateForm(RFController.FileType.JSON_OBJECT, formJsonObject, formData.toString(), formView, formListener);
        } else {
            mController.generateForm(RFController.FileType.JSON_OBJECT, formJsonObject, null, formView, formListener);
        }
    }

    /**
     * This method will take the form json and renders the form.
     * This method will prepopulate the field with the formData.
     *
     * @param formJsonObject
     * @param formData       - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithJsonObject(JSONObject formJsonObject, View formView, RFFormListener formListener, String formData) {
        if (isFormGenerated) {
            return;
        }

        if (!validateData(formJsonObject, formView, formListener)) {
            return;
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        isFormGenerated = true;
        mController.generateForm(RFController.FileType.JSON_OBJECT, formJsonObject, formData, formView, formListener);
    }

    /**
     * This method will take the form json and renders the form.
     *
     * @param formJsonObject
     * @param formView
     * @param formListener
     */
    public void generateFormWithJsonObject(JSONObject formJsonObject, View formView, RFFormListener formListener, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithJsonObject(formJsonObject, formView, formListener);
    }

    /**
     * This method will take the form json and renders the form.
     * This method will prepopulate the field with the formData.
     *
     * @param formJsonObject
     * @param formData       - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithJsonObject(JSONObject formJsonObject, View formView, RFFormListener formListener, JSONObject formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithJsonObject(formJsonObject, formView, formListener, formData);
    }

    /**
     * This method will take the form json and renders the form.
     * This method will prepopulate the field with the formData.
     *
     * @param formJsonObject
     * @param formData       - this contain the data of the fields in the form.
     * @param formView
     * @param formListener
     */
    public void generateFormWithJsonObject(JSONObject formJsonObject, View formView, RFFormListener formListener, String formData, ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        registerCustomViews(customElements, customFields);
        generateFormWithJsonObject(formJsonObject, formView, formListener, formData);
    }


    /**
     * Returns the response related to that key. If the key name is not related to any form elements or null we should throw custom exception.
     *
     * @param keyName key name can be pageKeyName/sectionKeyName/fieldKeyName
     * @return response (in the form of json string) . If the key name is null we should return the whole form response.
     */
    public String getResponseForKey(String keyName) {
        if (mController == null) {
            throw new RFNullFormException();
        }
        if (keyName == null) {
            throw new RFInvalidKeyName();
        }
        return mController.getResponseForKey(keyName);
    }

    /**
     * This method will return the entire form response.
     *
     * @return String (in the form of json string)
     */
    public String getFormResponse() {
        if (mController == null) {
            throw new RFNullFormException();
        }

        return mController.getFormResponse();
    }

    /**
     * Returns the current page state.
     *
     * @return BEGIN/MIDDLE/END
     */
    public RFPagesStates getCurrentPageState() {
        if (mController == null) {
            throw new RFNullFormException();
        }

        return mController.getCurrentPageState();
    }

    /**
     * Used for navigation to particular field/section/page. If the key name is field/section we should focus that view too.
     * If the keyname is not related to any form element we should throw exception
     *
     * @param keyName keyName can be pageKeyName/sectionKeyName/fieldKeyName
     */
    public void navigateTo(String keyName) {
        if (mController == null) {
            throw new RFNullFormException();
        }

        if (keyName == null) {
            throw new RFInvalidKeyName();
        }

        mController.navigateTo(keyName);
    }

    /**
     * This method is used for moving to next page.
     *
     * @return RFPageStates  - returns page state
     */
    public RFPagesStates moveToNextPage() {
        if (mController == null) {
            throw new RFNullFormException();
        }

        return mController.moveToNextPage();
    }

    /**
     * This method is used for moving to previous page.
     *
     * @return RFPageStates  - returns page state
     */
    public RFPagesStates moveToPreviousPage() {
        if (mController == null) {
            throw new RFNullFormException();
        }

        return mController.moveToPreviousPage();
    }

    /**
     * This method is used for mapping custom field to a  particular type.
     * If the user calls the register field more than once, if there are any duplicates previous values will be replaced with new ones.
     * If the user calls register field after generateForm we should throw exception.
     */
    public void registerFields(HashMap<String, Class> customFields) {

        if (isFormGenerated) {
            throw new RFRegisterException(RFUtils.getString(mContext, R.string.register_field_exception));
        }

        if (customFields == null) {
            throw new NullPointerException();
        }

        if (mContext == null) {
            throw new RFNullContextException();
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        mController.registerFields(customFields);
    }

    /**
     * This method is used for mapping custom element to a  particular type.
     * If the user calls the registerElements more than once, if there are any duplicates previous values will be replaced with new ones.
     * If the user calls registerElements after generateForm we should throw exception.
     *
     * @param customElements
     */
    public void registerElements(ArrayList<Class> customElements) {

        if (isFormGenerated) {
            throw new RFRegisterException(RFUtils.getString(mContext, R.string.register_element_exception));
        }

        if (customElements == null) {
            throw new NullPointerException();
        }

        if (mContext == null) {
            throw new RFNullContextException();
        }

        if (mController == null) {
            mController = new RFController(mContext);
        }

        mController.registerElements(customElements);
    }

    /**
     * This method is used for registering the context of the activity
     *
     * @param context
     */
    // NOTE: Android related method.
    public void registerContext(Activity context) {
        mContext = context;
    }

    /**
     * This method will validates the data given by the user and calls onParseError if the form listener is not null.
     *
     * @param formJsonObject
     * @param formView
     * @param formListener
     * @return If the data is invalid it will return false else true
     */
    private boolean validateData(String formJsonObject, View formView, RFFormListener formListener) {
        if (mContext == null) {
            throw new RFNullContextException();
        }

        if (!RFUtils.isInstance(ViewGroup.class, formView)) {
            throw new RFInvalidFormView();
        }

        String error = null;
        if (formJsonObject == null) {
            error = RFUtils.getString(mContext, R.string.form_json_null);
        } else if (formView == null) {
            error = RFUtils.getString(mContext, R.string.form_view_null);
        }

        if (error == null) {
            return true;
        }

        RFUtils.throwParseError(formListener, error);

        return false;
    }

    /**
     * This method will validates the data given by the user and calls onParseError if the form listener is not null.
     *
     * @param formJsonObject
     * @param formView
     * @param formListener
     * @return If the data is invalid it will return false else true
     */
    private boolean validateData(JSONObject formJsonObject, View formView, RFFormListener formListener) {
        if (mContext == null) {
            throw new RFNullContextException();
        }

        if (!RFUtils.isInstance(ViewGroup.class, formView)) {
            throw new RFInvalidFormView();
        }

        String error = null;
        if (formJsonObject == null) {
            error = RFUtils.getString(mContext, R.string.form_json_null);
        } else if (formView == null) {
            error = RFUtils.getString(mContext, R.string.form_view_null);
        }

        if (error == null) {
            return true;
        }

        RFUtils.throwParseError(formListener, error);

        return false;
    }

    /**
     * This method is used for registering the custom elements and fields which are passed through generate method.
     */
    private void registerCustomViews(ArrayList<Class> customElements, HashMap<String, Class> customFields) {
        if (customElements != null) {
            registerElements(customElements);
        }

        if (customFields != null) {
            registerFields(customFields);
        }
    }

    /**
     * This method is used for setting the form in the view mode
     */
    public void setViewModeToForm() {
        mController.setViewModeToForm();
    }

    public void modifyStyleProperties(HashMap<String,HashMap<String,Boolean>> data)
    {
        mController.modifyStyleProperties(data);

    }


}