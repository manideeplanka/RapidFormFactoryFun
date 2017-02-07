package com.rapidBizApps.lavasa.Constants;

/**
 * Created by cdara on 30-11-2015.
 */

/**
 * Use: This class contains all the constants related to the form Json.
 * Note: Declare all the form properties which are exposed to user in lowercase to support case insensitive of form properties.
 */
public class RFConstants {

    // Below constants  are used in the high level parser supported by the Library.
    public static final String FC_META = "meta";
    public static final String FC_NAME = "name";
    public static final String FC_TITLE = "title";
    public static final String FC_VERSION = "version";

    // Below constants are used in the response json of the form
    public static final String FC_TYPE = "type";
    public static final String FC_DATA = "data";
    public static final String FC_ERRORS = "error";

    // Below constants are the four parts of the Forms json
    public static final String FC_VALIDATION = "validation";
    public static final String FC_LAYOUT = "layout";
    public static final String FC_STYLES = "styles";
    public static final String FC_SCHEMA = "schema";
    public static final String FC_BEHAVIOUR = "behaviour";

    //Below constants are used in the RFLocation cutsom view response json
    public static final String FC_LATITUDE = "latitude";
    public static final String FC_LONGITUDE = "longitude";

    // Below constants are not exposed to User.

    // this constant is used keeping the prefix for the default section key name.
    public static final String FC_DEFAULT_SECTION = "defaultSection";
    public static final String FC_PAGE_TITLE = "pageTitle";
    public static final String FC_SECTION_TITLE = "sectionTitle";
    public static final String FC_FORM_TITLE = "formTitle";
    public static final String FC_FIELD_TITLE = "fieldTitle";

}
