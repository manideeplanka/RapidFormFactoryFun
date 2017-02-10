package com.rapidBizApps.lavasa.Listeners;

import android.content.Context;
import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Constants.RFValidationConstants;
import com.rapidBizApps.lavasa.Exceptions.RFNullBaseElementException;
import com.rapidBizApps.lavasa.Models.RFBaseModel;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFBehaviourApplier;
import com.rapidBizApps.lavasa.RFUtils;
import com.rapidBizApps.lavasa.Validations.RFValidator;
import com.rapidBizApps.lavasa.Views.RFBaseElement;
import com.rapidBizApps.lavasa.Views.RFForm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cdara on 03-02-2016.
 */

/**
 * Use: this is used for invoking the validator and parsing the response of the validation method.
 */
public final class RFElementEventListenerClass implements RFElementEventListener {

    private static RFFormListener sFormListener;
    private static RFForm sRFView;

    private final String TAG = RFElementEventListenerClass.class.getName();
    private RFValidator mValidatorObject;
    private RFBehaviourApplier mBehaviourApplierObject;
    private Context mContext;


    public RFElementEventListenerClass(Context context, RFBaseElement element) {
        mValidatorObject = new RFValidator(context);
        mBehaviourApplierObject = new RFBehaviourApplier();
        mContext = context;


        if (element != null && RFUtils.isInstance(RFElementModel.class, element.getModel())) {
            RFElementModel elementModel = (RFElementModel) element.getModel();

            // This method will execute only on page edit
            if (elementModel.getValue() != null) {
                // TODO: 10/2/17 removing this helps with dependant fields
//                applyValidation(element, RFValidationConstants.VC_ON_CHANGE);
            }
        }
    }

    /**
     * This method is used for setting the data to the model and invoking apply validation.
     *
     * @param element
     * @param data
     */
    @Override
    public void onChange(RFBaseElement element, String data) {
        if (element != null) {
            RFBaseModel baseModel = element.getModel();

            if (RFUtils.isInstance(RFElementModel.class, baseModel)) {

                RFElementModel elementModel = (RFElementModel) element.getModel();
                if (elementModel.getValue() != null && elementModel.getValue().trim().length() > 0)
                    elementModel.setErrorMessage(null);

                if (!(elementModel.getValue() != null && data != null && elementModel.getValue().replace("\"", "").equals(data.replace("\"", "")))) {
                    elementModel.setValue(data);
                    applyValidation(element, RFValidationConstants.VC_ON_CHANGE);

                    if (sFormListener != null) {
                        sFormListener.onChange(convertElementDataToJsonString(elementModel));
                    }
                }

            } else if (RFUtils.isInstance(RFPageModel.class, baseModel) || RFUtils.isInstance(RFSectionModel.class, baseModel)) {
                Log.e(TAG, "Page or section change called for" + baseModel.getId());
            }

        } else {
            throw new RFNullBaseElementException();
        }
    }

    // This will invoke applyValidation
    @Override
    public void onBeginEditing(RFBaseElement element) {
        if (element != null) {
            applyValidation(element, RFValidationConstants.VC_ON_FOCUS);
        } else {
            throw new RFNullBaseElementException();
        }
    }

    // This will invoke apply validation
    @Override
    public void onEndEditing(RFBaseElement element) {
        if (element != null) {
            applyValidation(element, RFValidationConstants.VC_ON_FOCUS_LOST);
        } else {
            throw new RFNullBaseElementException();
        }
    }

    /**
     * This method will invoke the validation method in RFValidator class.
     *
     * @param element
     * @param validationType
     */
    private void applyValidation(final RFBaseElement element, String validationType) {
        String javascriptMethodResponse = mValidatorObject.applyValidations(element, validationType);
        setResponseToModelAndView(element, javascriptMethodResponse);
    }

    /**
     * This method is used for setting the validation response to model.
     *
     * @param element
     * @param javascriptMethodResponse
     */
    private void setResponseToModelAndView(RFBaseElement element, String javascriptMethodResponse) {

        try {
            if (javascriptMethodResponse == null) {
                return;
            }

            JSONObject methodResponse = new JSONObject(javascriptMethodResponse);
            // This will check if the response has error message or not. Based on that it will set data to model and to view.
            if (!methodResponse.isNull(RFValidationConstants.VC_ERROR_MESSAGE) && methodResponse.getString(RFValidationConstants.VC_ERROR_MESSAGE).trim().length() != 0) {
                ((RFElementModel) element.getModel()).setErrorMessage(methodResponse.getString(RFValidationConstants.VC_ERROR_MESSAGE));
            } else {
                ((RFElementModel) element.getModel()).setErrorMessage(null);
            }

            element.setError(((RFElementModel) element.getModel()).getErrorMessage());

            if (!methodResponse.isNull(RFValidationConstants.VC_BEHAVIOUR)) {
                String behaviourKey = methodResponse.get(RFValidationConstants.VC_BEHAVIOUR).toString();
                mBehaviourApplierObject.applyBehaviour(behaviourKey, element.getModel());
                if (element.getView() != null && sRFView != null) {
                    sRFView.refreshCurrentPage();
                }
            } else if (!methodResponse.isNull(RFValidationConstants.VC_DYNAMIC_BEHAVIOUR)) {
                String behaviourKey = methodResponse.get(RFValidationConstants.VC_DYNAMIC_BEHAVIOUR).toString();
                String nextFocusKey = null;
                JSONObject jsonObject = new JSONObject(behaviourKey);

              if (jsonObject.has(RFValidationConstants.VC_DYNAMIC_DATA)) {
                    nextFocusKey = dynamicBehaviourIfData(element, nextFocusKey, jsonObject, RFValidationConstants.VC_DYNAMIC_DATA);
                } else if (jsonObject.has(RFValidationConstants.VC_DYNAMIC_OPTIONS)) {
                    nextFocusKey = dynamicBehaviourIfData(element, nextFocusKey, jsonObject, RFValidationConstants.VC_DYNAMIC_OPTIONS);
                }

                if (element.getView() != null && sRFView != null) {
                    if (!element.getData().equalsIgnoreCase("")) {
                        sRFView.refreshCurrentPage();
                        sRFView.navigateTo(nextFocusKey);
                    } else {
                        sRFView.navigateTo(element.getModel().getId());
                    }
                }
            }
        } catch (JSONException exception) {
            Log.e(TAG, "setResponseToModelAndView: " + exception.toString());
        }
    }

    // TODO: 22/12/16 @mlanka add documentation after figuring out what's happening
    private String dynamicBehaviourIfData(RFBaseElement element, String nextFocusKey, final JSONObject jsonObject, String type) throws JSONException {
        JSONObject dataJsonObject = null;
        String findForKey = "";

        if (type.equalsIgnoreCase(RFValidationConstants.VC_DYNAMIC_DATA)) {
            dataJsonObject = jsonObject.getJSONObject(RFValidationConstants.VC_DYNAMIC_DATA);
        } else if (type.equalsIgnoreCase(RFValidationConstants.VC_DYNAMIC_OPTIONS)) {
            dataJsonObject = jsonObject.getJSONObject(RFValidationConstants.VC_DYNAMIC_OPTIONS);
            findForKey = jsonObject.getString(RFValidationConstants.FIND_FOR_KEY);
        }

        Log.d(TAG, "dynamicBehaviourIfData: dataJsonObject:" + dataJsonObject);

        Iterator<String> iter = dataJsonObject.keys();

        while (iter.hasNext()) {
            final String key = iter.next();
            ArrayList<RFSectionModel> sectionsList = ((RFElementModel) element.getModel()).getSection().getPage().getSections();
            for (int i = 0; i < sectionsList.size(); i++) {
                ArrayList<RFElementModel> elementsList = sectionsList.get(i).getFields();
                for (int j = 0; j < elementsList.size(); j++) {

                    final RFElementModel rfElementModel = elementsList.get(j);
                    if (rfElementModel.getId().equals(key)) {
                        Log.d(TAG, "dynamicBehaviourIfData: current key:" + key);
                            if (type.equalsIgnoreCase(RFValidationConstants.VC_DYNAMIC_DATA)) {
                                String value = "dynamic";
                                rfElementModel.setValue(value);
                            } else if (type.equalsIgnoreCase(RFValidationConstants.VC_DYNAMIC_OPTIONS)) {
                                //get the data from the app
                                sFormListener.loadDynamicData(key, findForKey, new FoundDynamicDataCallback() {
                                    @Override
                                    public void callback(List<String> list) {
                                        JSONArray jsonArray = null;
                                        try {
                                            jsonArray = createJsonArrayFromList(list);
                                        } catch (JSONException e) {
                                            Log.e(TAG, "callback: " + e.toString());
                                        }
                                        LinkedHashMap<String, String> value = getDataFromJson(jsonArray);
                                        Log.d(TAG, "callback: elementID:" + rfElementModel.getId());
                                        rfElementModel.setJsonData(value);
                                        rfElementModel.setValue(null); //why this?
                                    }
                                });
                            }
                            nextFocusKey = key;
                        Log.d(TAG, "dynamicBehaviourIfData: nextFocusKey: " + nextFocusKey);
                    }
                }
            }
        }
        return nextFocusKey;
    }

    private JSONArray createJsonArrayFromList(List<String> strings) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        //JSONArray of JSONObjects to stay in line with current implementation
        for (int i = 0; i < strings.size(); i++) {
            jsonArray.put(new JSONObject().put(i + "", strings.get(i)));
        }

        Log.d(TAG, "createJsonArrayFromList: " + jsonArray.toString());
        return jsonArray;
    }

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


    private boolean isJson(String behaviourValue) {
        try {
            new JSONObject(behaviourValue);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static RFFormListener getFormListener() {
        return sFormListener;
    }

    /**
     * This will set the form listener
     *
     * @param formListener
     */
    public void setFormListener(RFFormListener formListener) {
        sFormListener = formListener;
    }

    /**
     * This will set the RFForm
     *
     * @param rfFormView
     */
    public void setRFView(RFForm rfFormView) {
        sRFView = rfFormView;
    }

    public String convertElementDataToJsonString(RFElementModel model) {
        JSONObject responseJson = new JSONObject();
        try {
            JSONObject fieldResponseJson = new JSONObject();
            fieldResponseJson.put(RFConstants.FC_DATA, model.getValue());
            fieldResponseJson.put(RFConstants.FC_ERRORS, model.getErrorMessage());

            responseJson.put(model.getId(), fieldResponseJson);

            return responseJson.toString();
        } catch (JSONException e) {
            Log.e(TAG, " convertElementDataToJsonString" + e);
        }

        return null;
    }

}
