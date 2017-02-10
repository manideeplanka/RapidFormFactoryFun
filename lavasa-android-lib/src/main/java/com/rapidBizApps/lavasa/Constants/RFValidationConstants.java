package com.rapidBizApps.lavasa.Constants;

/**
 * Created by cdara on 30-11-2015.
 */

/**
 * Use: This class contains the constants related to the validation.
 * Note: Declare all the validation properties in lowercase to support case insensitive of validation properties.
 */
public interface RFValidationConstants {

    // Below constants are specified in the validation block of form json.
    String VC_ON_FOCUS = "on_focus";
    String VC_ON_FOCUS_LOST = "on_focus_lost";
    String VC_ON_CHANGE = "on_change";
    String VC_REQUIRED = "required";
    String VC_METHOD_NAME = "method_name";
    String VC_FUNCTION = "function";

    // Below constants are used in parsing the response of the  java script
    String VC_DATA = "data";
    String VC_OPTIONS = "options";
    String VC_ERRORS = "errors";
    String VC_ERROR_MESSAGE = "error_message";
    String VC_EDITABILITY = "editability";
    String VC_VISIBILITY = "visibility";
    String VC_SUCCESS = "success";

    //used to identify that data needs to be added to the form from an external source
    String VC_DYNAMIC_OPTIONS = "dynamic_options";
    String VC_DYNAMIC_DATA = "dynamic_data";
    String FIND_FOR_KEY = "findForKey";

    String VC_BEHAVIOUR = "behaviour";
    String VC_DYNAMIC_BEHAVIOUR = "dynamic_behaviour";
}
