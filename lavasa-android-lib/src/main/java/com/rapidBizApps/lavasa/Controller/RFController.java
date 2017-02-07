package com.rapidBizApps.lavasa.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.rapidBizApps.lavasa.Exceptions.RFInvalidKeyName;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFPagesStates;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFParser;
import com.rapidBizApps.lavasa.ParseAndRender.RFRegister;
import com.rapidBizApps.lavasa.ParseAndRender.RFViewGenerator;
import com.rapidBizApps.lavasa.RFUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cdara on 25-01-2016.
 */

/**
 * Use: It receives all the call from the builder and navigates to corresponding class.
 */
public final class RFController {

    private final int PERMISSIONS_REQUEST_FILE = 1;

    private RFFormModel mForm;
    private Context mContext;
    private RFViewGenerator mViewGenerator;
    private RFRegister mRFRegister;
    private RFFormListener mFormListener;

    protected RFController(Context context) {
        mContext = context;
        mRFRegister = new RFRegister(this);
        mViewGenerator = new RFViewGenerator(this);
    }

    /**
     * Use: Retrieving the form json from any kind of input(string/filepath/filename/jsonObject)
     *
     * @param typeOfInput
     * @param formJson
     * @param formData     - prefilled data
     * @param formListener
     */
    protected void generateForm(FileType typeOfInput, Object formJson, String formData, View formView, RFFormListener formListener) {

        mFormListener = formListener;

        //  Based on the form input type it gets the form json object.
        JSONObject formJsonObject = null;
        JSONObject formDataJsonObject = null;

        // This will compares the input type and gets the json from that input data
        if (typeOfInput.equals(FileType.STRING)) {
            formJsonObject = getJsonFromString((String) formJson);
        } else if (typeOfInput.equals(FileType.FILEPATH)) {
            formJsonObject = getJsonFromFilePath((String) formJson);
        } else if (typeOfInput.equals(FileType.FILE_NAME)) {
            formJsonObject = getJsonFromAssets((String) formJson);
        } else if (typeOfInput.equals(FileType.JSON_OBJECT)) {
            formJsonObject = (JSONObject) formJson;
        }

        // get the form data json from a json string
        formDataJsonObject = getJsonFromString(formData);

        generateFormWithJsonObject(formJsonObject, formDataJsonObject);

        // sends callback to the user before rendering the form for registering views.
        if (mForm != null && formListener != null) {
            formListener.onBeforeFormRender();
        }

        // If the mForm is null it means there exists a parser error. If the parse error occurred we not allowing to rendering the form.
        if (mForm != null) {
            renderForm(formView);
        }
    }

    /**
     * Use: This method is used for invoking the RFParser
     *
     * @param formJson
     * @param formData
     */
    private void generateFormWithJsonObject(JSONObject formJson, JSONObject formData) {
        RFParser parserObject = new RFParser(this);
        mForm = parserObject.parseJson(formJson, formData);

    }

    /**
     * Use: This method is used for invoking the RFViewGenerator
     */
    private void renderForm(View view) {
        mViewGenerator.createFormContainer(view);
    }

    /**
     * This method will convert the string to json object and returns the json object.
     *
     * @param jsonObjectString
     * @return
     */
    private JSONObject getJsonFromString(String jsonObjectString) {
        if (jsonObjectString == null) {
            return null;
        }
        try {
            return new JSONObject(jsonObjectString);
        } catch (Exception e) {
            RFUtils.throwParseError(mFormListener, e.toString());
        }
        return null;
    }

    /**
     * This method will retrieve the json from file.
     *
     * @param filePath
     * @return
     */
    private JSONObject getJsonFromFilePath(String filePath) {
        try {

            String form = null;
            InputStream fileInputStream = null;

            // Below code is for requesting the file access permission for MARSHMALLA devices.
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FILE);
                return null;
            }

            File jsonFile = new File(filePath);
            fileInputStream = new FileInputStream(jsonFile);
            StringBuilder data = new StringBuilder();

            // this loop will append the data in the input stream to data
            while (fileInputStream.available() > 0) {
                data.append((char) fileInputStream.read());
            }

            if (data != null) {
                form = data.toString();
            }

            return getJsonFromString(form);
        } catch (Exception e) {
            RFUtils.throwParseError(mFormListener, e.toString());
        }
        return null;
    }

    /**
     * Retrieves the json from the assets file.
     *
     * @param fileName
     * @return
     */
    private JSONObject getJsonFromAssets(String fileName) {
        try {

            String form = null;
            InputStream fileInputStream = mContext.getAssets().open(fileName);
            StringBuilder data = new StringBuilder();

            // this loop will append the data in the input stream to data
            while (fileInputStream.available() > 0) {
                data.append((char) fileInputStream.read());
            }

            if (data != null) {
                form = data.toString();
            }

            return getJsonFromString(form);
        } catch (Exception e) {
            RFUtils.throwParseError(mFormListener, e.toString());
        }

        return null;
    }

    /**
     * Returns the keyname name related data. If the keyname is not related to any of the form elements or null it will throw exception.
     *
     * @param keyName can be pageKeyName/sectionKeyName/fieldKeyName
     * @return error (in the form of json string) when the keyname is incorrect
     */
    protected String getResponseForKey(String keyName) {
        JSONObject response = null;
        response = mForm.getResponseData(keyName);
        if (response != null) {
            return response.toString();
        }
        throw new RFInvalidKeyName();
    }

    /**
     * This will returns the entire form response.
     *
     * @return response (in the form of json string)
     */
    public String getFormResponse() {

        if (mForm == null) {
            return null;
        }
        applyValidations();
        JSONObject responseJson = mForm.getResponseData();
        if (responseJson != null) {
            return responseJson.toString();
        }

        return null;
    }

    /**
     * Returns the current page state.
     *
     * @return BEGIN/MIDDLE/END
     */
    protected RFPagesStates getCurrentPageState() {
        return mForm.getCurrentPageState();
    }

    /**
     * Used for navigation to particular field/section/page. If the keyname is not related to any of the form elements we should throw error.
     *
     * @param keyName keyName can be pageKeyName/sectionKeyName/fieldKeyName
     * @return
     */
    protected void navigateTo(String keyName) {
        mViewGenerator.navigateTo(keyName);
    }

    /**
     * This method will move to next page.
     *
     * @return returns page state
     */
    protected RFPagesStates moveToNextPage() {
        return mViewGenerator.moveToNextPage();
    }

    /**
     * This method will move the form to prev page.
     *
     * @return returns page state
     */
    protected RFPagesStates moveToPreviousPage() {
        return mViewGenerator.moveToPreviousPage();
    }

    /**
     * This method is used for mapping custom field to a  particular type.
     * If the user calls the register field more than once, if there are any duplicates previous values will be replaced with new ones.
     * If the user calls register field after generateForm we should throw exception.
     */
    protected void registerFields(HashMap<String, Class> customFields) {
        mRFRegister.registerFields(customFields);
    }

    /**
     * This method is used for mapping custom element to a  particular type.
     * If the user calls the registerElements more than once, if there are any duplicates previous values will be replaced with new ones.
     * If the user calls registerElements after generateForm we should throw exception.
     *
     * @param customElements
     */
    protected void registerElements(ArrayList<Class> customElements) {
        mRFRegister.registerElements(customElements);
    }


    /**
     * This method is used for setting the form in the view mode
     */
    protected void setViewModeToForm() {
        for (int i = 0; i < mForm.getPages().size(); i++) {
            RFPageModel currentPage = mForm.getPages().get(i);

            for (int j = 0; j < currentPage.getSections().size(); j++) {

                RFSectionModel currentSection = currentPage.getSections().get(j);

                for (int k = 0; k < currentSection.getFields().size(); k++) {
                    currentSection.getFields().get(k).getStyle().setEditability(false);
                }
            }

        }
    }


    /**
     * This method returns the register object
     *
     * @return
     */
    public RFRegister getRegister() {
        return mRFRegister;
    }

    /**
     * This will returns the context object
     *
     * @return
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * This will returns form listener.
     *
     * @return
     */
    public RFFormListener getFormListener() {
        return mFormListener;
    }

    /**
     * This will returns form model object
     *
     * @return
     */
    public RFFormModel getForm() {
        return mForm;
    }

    // this enum will contains all the possible ways of getting the form json.
    protected enum FileType {
        STRING, FILEPATH, FILE_NAME, JSON_OBJECT, URL
    }

    protected void modifyStyleProperties(HashMap<String, HashMap<String, Boolean>> data) {


        for (int i = 0; i < mForm.getPages().size(); i++) {
            RFPageModel pageModel = mForm.getPages().get(i);
            for (int j = 0; j < pageModel.getSections().size(); j++) {
                RFSectionModel rfSectionModel = pageModel.getSections().get(j);
                if (data.containsKey(rfSectionModel.getId())) {
                    rfSectionModel.getStyle().setVisibility(true);
                    boolean isEdit = data.get(rfSectionModel.getId()).get("editable");
                    for (int k = 0; k < rfSectionModel.getFields().size(); k++) {
                        rfSectionModel.getFields().get(k).getStyle().setEditability(isEdit);
                    }
                } else {
                    rfSectionModel.getStyle().setVisibility(false);
                }


            }


        }


    }

    protected void applyValidations() {
        mViewGenerator.applyValidations();
    }
}
