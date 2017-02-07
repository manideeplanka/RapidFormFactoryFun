package com.rapidBizApps.lavasa.Models;

/**
 * Created by cdara on 08-03-2016.
 */
public enum RFPagesStates {

    BEGIN(1),
    MIDDLE(2),
    END(4),
    SINGLE(5);

    private int state;

    RFPagesStates(int pageState) {
        this.state = pageState;
    }

    public int getState() {
        return state;
    }
}
