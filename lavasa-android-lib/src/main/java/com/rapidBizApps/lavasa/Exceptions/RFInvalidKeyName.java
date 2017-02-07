package com.rapidBizApps.lavasa.Exceptions;

/**
 * Created by cdara on 05-04-2016.
 */
public class RFInvalidKeyName extends RuntimeException {

    public RFInvalidKeyName() {
        super("KeyName is invalid.");
    }
}
