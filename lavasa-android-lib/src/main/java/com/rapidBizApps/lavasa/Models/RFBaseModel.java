package com.rapidBizApps.lavasa.Models;

/**
 * Created by cdara on 20-01-2016.
 */

import java.util.HashMap;

/**
 * Use: This contains the common properties of the form, page,section,field. Form/Page/Section/Field will extend this class.
 */
public class RFBaseModel {

    // Need to store the key name in id variable
    protected String id;

    // This will contain the title
    protected String name;

    protected RFStyleModel style;
    protected RFLayoutModel layout;
    protected RFValidationModel validation;
    protected HashMap<String, HashMap<RFBaseModel, RFBehaviourModel>> behaviour;

    public RFBaseModel() {
        setLayout(new RFLayoutModel());
        setStyle(new RFStyleModel());
        behaviour = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RFStyleModel getStyle() {
        return style;
    }

    public void setStyle(RFStyleModel style) {
        this.style = style;
    }

    public RFLayoutModel getLayout() {
        return layout;
    }

    public void setLayout(RFLayoutModel layout) {
        this.layout = layout;
    }

    public RFValidationModel getValidation() {
        return validation;
    }

    public void setValidation(RFValidationModel validation) {
        this.validation = validation;
    }

    public HashMap<String, HashMap<RFBaseModel, RFBehaviourModel>> getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(HashMap<String, HashMap<RFBaseModel, RFBehaviourModel>> behaviour) {
        this.behaviour = behaviour;
    }
}
