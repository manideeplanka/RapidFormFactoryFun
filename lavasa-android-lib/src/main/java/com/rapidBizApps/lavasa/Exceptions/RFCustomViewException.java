package com.rapidBizApps.lavasa.Exceptions;

/**
 * Created by cdara on 15-03-2016.
 */
// TODO: Need to  discuss
public class RFCustomViewException extends RuntimeException {

    public RFCustomViewException(String msg) {
        super(msg);
    }

    public RFCustomViewException(Exception e) {
        super(e);
    }
}
