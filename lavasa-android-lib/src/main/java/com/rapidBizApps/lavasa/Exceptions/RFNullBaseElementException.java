package com.rapidBizApps.lavasa.Exceptions;

/**
 * Created by cdara on 18-04-2016.
 */

public class RFNullBaseElementException extends RuntimeException {

    public RFNullBaseElementException() {
        super("Base Element shouldn't be null");
    }
}

