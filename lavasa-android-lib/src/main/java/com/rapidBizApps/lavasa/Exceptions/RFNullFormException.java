package com.rapidBizApps.lavasa.Exceptions;

/**
 * Created by cdara on 15-03-2016.
 */
// TODO: Need to  discuss
public class RFNullFormException extends RuntimeException {

    public RFNullFormException() {
        super("Form doesn't exists. For the form generation please call generate form.");
    }
}
