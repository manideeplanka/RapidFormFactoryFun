package com.rapidBizApps.lavasa.Exceptions;

/**
 * Created by cdara on 14-03-2016.
 */
// TODO: Need to  discuss
public class RFNullContextException extends RuntimeException {

    public RFNullContextException() {
        super("Context should be registered before calling generate form.");
    }
}
