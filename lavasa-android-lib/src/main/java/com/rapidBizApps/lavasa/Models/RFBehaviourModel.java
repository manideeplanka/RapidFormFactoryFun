package com.rapidBizApps.lavasa.Models;

/**
 * Created by cdara on 22-03-2016.
 */
public class RFBehaviourModel {
    private boolean visibility = true;
    private boolean editability = true;
    private String data;

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isEditability() {
        return editability;
    }

    public void setEditability(boolean editability) {
        this.editability = editability;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
