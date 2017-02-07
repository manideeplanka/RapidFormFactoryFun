package com.rapidBizApps.lavasa.Models;

import java.util.HashMap;

/**
 * Created by cdara on 20-01-2016.
 */
public final class RFValidationModel {
    // Key is method name and the value is function.
    private HashMap<String, String> onFocus;

    // Key is method name and the value is function.
    private HashMap<String, String> onFocusLost;

    // Key is method name and the value is function.
    private HashMap<String, String> onValueChange;

    // Key is method name and the value is function.
    private HashMap<String, String> onChange;

    private boolean isRequired;

    public HashMap<String, String> getOnFocus() {
        return onFocus;
    }

    public void setOnFocus(HashMap<String, String> onFocus) {
        this.onFocus = onFocus;
    }

    public HashMap<String, String> getOnFocusLost() {
        return onFocusLost;
    }

    public void setOnFocusLost(HashMap<String, String> onFocusLost) {
        this.onFocusLost = onFocusLost;
    }

    public HashMap<String, String> getOnValueChange() {
        return onValueChange;
    }

    public void setOnValueChange(HashMap<String, String> onValueChange) {
        this.onValueChange = onValueChange;
    }

    public HashMap<String, String> getOnChange() {
        return onChange;
    }

    public void setOnChange(HashMap<String, String> onChange) {
        this.onChange = onChange;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        this.isRequired = required;
    }
}
