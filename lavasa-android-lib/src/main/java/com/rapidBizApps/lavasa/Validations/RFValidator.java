package com.rapidBizApps.lavasa.Validations;

import android.support.annotation.Nullable;
import android.util.Log;

import com.rapidBizApps.lavasa.Constants.RFValidationConstants;
import com.rapidBizApps.lavasa.Models.RFBaseModel;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.Models.RFValidationModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFBehaviourApplier;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rapidBizApps.lavasa.Views.RFBaseElement;
import com.rapidBizApps.lavasa.Views.RFPage;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cdara on 17-12-2015.
 */

/**
 * Use: This class is used for executing the java script validation methods of the fields
 */
public final class RFValidator {

    private String TAG = RFValidator.class.getName();
    private android.content.Context mContext;
    private RFBehaviourApplier mBehaviourApplierObject;

    public RFValidator(android.content.Context context) {
        mContext = context;
        mBehaviourApplierObject = new RFBehaviourApplier();
    }

    /**
     * This will verify whether the element is page or field and calls corresponding validations.
     *
     * @param element
     * @param validationType
     */
    public String applyValidations(RFBaseElement element, String validationType) {
        if (element == null) {
            return null;
        }

        RFValidationModel validation = element.getModel().getValidation();

        if (element instanceof RFPage && validationType == null) {
            applyDefaultFieldValidation((RFPageModel) element.getModel());
            return null;
        }


        return getValidationResult(element.getModel(), validationType, validation);
    }

    /**
     * Based on the type of validation  this will retrieves method and function and send the data to javaScriptEvaluator for execution.
     *
     * @param elementModel
     * @param validationType
     * @param validation
     * @return
     */
    @Nullable
    private String getValidationResult(RFBaseModel elementModel, String validationType, RFValidationModel validation) {
        HashMap<String, String> methodDetails = null;
        if (validation == null) {
            return null;
        }
        // retrieves the method details based on the type
        switch (validationType) {
            case RFValidationConstants.VC_ON_FOCUS:
                methodDetails = validation.getOnFocus();
                break;

            case RFValidationConstants.VC_ON_CHANGE:
                methodDetails = validation.getOnValueChange();
                break;

            case RFValidationConstants.VC_ON_FOCUS_LOST:

                methodDetails = validation.getOnFocusLost();
                break;
        }

        // Retrieves method name and function from method details
        if (methodDetails != null) {
            String methodName = null;
            String function = null;

            Map.Entry<String, String> methodDetailsMap = methodDetails.entrySet().iterator().next();
            methodName = methodDetailsMap.getKey();
            function = methodDetailsMap.getValue();

            try {

                JSONObject responseJson = getFormResponseFromModel(elementModel);
                return javaScriptEvaluator(methodName, function, responseJson);
            } catch (Exception e) {
                Log.e(TAG + " applyValidations", e.toString());
                return null;
            }

        } else {
            // If there is no validation this will returns null
            return null;
        }
    }

    /**
     * This method will evaluate the java script code using the Rhino library.
     *
     * @param function
     * @param methodName
     * @return
     */
    private String javaScriptEvaluator(String methodName, String function, JSONObject parameter) {
        Object[] params = new Object[]{parameter};

        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context
        Context rhinoContext = Context.enter();

        // Turn off optimization to make Rhino Android compatible
        rhinoContext.setOptimizationLevel(-1);

        Scriptable scope = rhinoContext.initStandardObjects();

        // Note the forth argument is 1, which means the JavaScript source has
        // been compressed to only one line using something like YUI
        rhinoContext.evaluateString(scope, function, "JavaScript", 1, null);

        // Get the functionName defined in JavaScriptCode
        Object functionObject = scope.get(methodName, scope);
        NativeObject nativeObject = null;
        if (functionObject instanceof Function) {
            Function jsFunction = (Function) functionObject;

            // Call the function with params
            Object jsResult = jsFunction.call(rhinoContext, scope, scope, params);
            nativeObject = ((NativeObject) jsResult);
        }

        JSONObject validationResponseObject = RFUtils.convertNativeObjectToJsonObject(nativeObject);
        Log.e(TAG,"validationResponseObject ::"+validationResponseObject);

        if (validationResponseObject == null) {
            return null;
        }
        return validationResponseObject.toString();
    }


    /**
     * This method is used for applying required field validations for a page.
     *
     * @param pageModel
     */
    public void applyDefaultFieldValidation(final RFPageModel pageModel) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<RFSectionModel> sections = pageModel.getSections();

                for (int sectionNumber = 0; sectionNumber < sections.size(); sectionNumber++) {
                    RFSectionModel currentSection = sections.get(sectionNumber);

                    ArrayList<RFElementModel> fields = currentSection.getFields();
                    for (int fieldNumber = 0; fieldNumber < fields.size(); fieldNumber++) {
                        RFElementModel currentElement = fields.get(fieldNumber);

                        // this will run the default java script validation function
                        String methodResponseJsonString = getValidationResult(currentElement, RFValidationConstants.VC_ON_CHANGE, currentElement.getValidation());
                        if (methodResponseJsonString != null && methodResponseJsonString.length() != 0) {
                            try {
                                JSONObject methodResponse = new JSONObject(methodResponseJsonString);

                                // This will check if the response has error message or not. Based on that it will set data to model and to view.
                                if (!methodResponse.isNull(RFValidationConstants.VC_ERROR_MESSAGE)) {
                                    currentElement.setErrorMessage(methodResponse.getString(RFValidationConstants.VC_ERROR_MESSAGE));
                                    // Already we got error from the default method so there is no need to do required field validation.
                                    continue;
                                } else {
                                    currentElement.setErrorMessage(null);
                                }

                                // Behaviour is not applying when the default method execution happens.
                            /*if (!methodResponse.isNull(RFValidationConstants.VC_BEHAVIOUR)) {
                                String behaviourKey = methodResponse.get(RFValidationConstants.VC_BEHAVIOUR).toString();
                                mBehaviourApplierObject.applyBehaviour(behaviourKey, currentElement);
                            }*/

                            } catch (JSONException e) {
                                Log.e(TAG + " applyDefaultFieldValidation", e.toString());
                            }
                        }

                        // this will apply required field validation
                        RFValidationModel fieldValidation = currentElement.getValidation();
                        if (fieldValidation != null && fieldValidation.isRequired() && (currentElement.getValue() == null || currentElement.getValue().trim().length() == 0)) {
                            currentElement.setErrorMessage(RFUtils.getString(mContext, R.string.required_field_error));
                        } else {
                            currentElement.setErrorMessage(null);
                        }

                    }
                }
            }
        };

        runnable.run();
    }

    /**
     * This will retrieves the total form response.
     *
     * @param baseModel
     * @return
     */
    private JSONObject getFormResponseFromModel(RFBaseModel baseModel) {
        RFFormModel formModel = null;

        if (RFUtils.isInstance(RFElementModel.class, baseModel)) {
            RFSectionModel sectionModel = ((RFElementModel) baseModel).getSection();
            RFPageModel pageModel = sectionModel.getPage();
            formModel = pageModel.getForm();
        } else if (RFUtils.isInstance(RFFormModel.class, baseModel)) {
            formModel = (RFFormModel) baseModel;
        } else if (RFUtils.isInstance(RFPageModel.class, baseModel)) {
            formModel = ((RFPageModel) baseModel).getForm();
        } else if (RFUtils.isInstance(RFSectionModel.class, baseModel)) {
            RFPageModel pageModel = ((RFSectionModel) baseModel).getPage();
            formModel = pageModel.getForm();
        }

        if (formModel != null) {
            return formModel.getResponseData();
        }

        return null;
    }
}
