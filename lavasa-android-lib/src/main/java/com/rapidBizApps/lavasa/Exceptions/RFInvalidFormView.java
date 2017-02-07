package com.rapidBizApps.lavasa.Exceptions;

/**
 * Created by cdara on 09-05-2016.
 */
public class RFInvalidFormView extends RuntimeException {

    public RFInvalidFormView() {
        super("Form view should be an instance of view group.");
    }
}
