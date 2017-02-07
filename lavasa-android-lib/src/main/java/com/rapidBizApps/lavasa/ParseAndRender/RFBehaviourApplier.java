package com.rapidBizApps.lavasa.ParseAndRender;

import com.rapidBizApps.lavasa.Models.RFBaseModel;
import com.rapidBizApps.lavasa.Models.RFBehaviourModel;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.RFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kkalluri on 3/22/2016.
 */
public class RFBehaviourApplier {

    /**
     * Based on the behaviourKey this method will set the behavioural data to respective models.
     *
     * @param behaviourKey
     * @param baseModel
     */
    public void applyBehaviour(String behaviourKey, RFBaseModel baseModel) {

        HashMap<String, HashMap<RFBaseModel, RFBehaviourModel>> behaviourMap = baseModel.getBehaviour();

        HashMap<RFBaseModel, RFBehaviourModel> elementBehaviour = behaviourMap.get(behaviourKey);

        if (elementBehaviour == null)
            return;

        Iterator iterator = elementBehaviour.entrySet().iterator();

        ArrayList<RFBaseModel> effectedModels = new ArrayList<>();
        ArrayList<RFBaseModel> effectedPageModels = new ArrayList<>();
        ArrayList<RFBaseModel> effectedSectionModels = new ArrayList<>();
        ArrayList<RFBaseModel> effectedFieldModels = new ArrayList<>();

        // This is for sorting the models based on the page type, section type and field type. So that we can manage visibility and editibility according to hierarchy
        while (iterator.hasNext()) {

            Map.Entry<RFBaseModel, RFBehaviourModel> pair = (Map.Entry<RFBaseModel, RFBehaviourModel>) iterator.next();
            RFBaseModel dependentModel = pair.getKey();

            if (RFUtils.isInstance(RFElementModel.class, dependentModel)) {
                effectedFieldModels.add(dependentModel);
            } else if (RFUtils.isInstance(RFSectionModel.class, dependentModel)) {
                effectedSectionModels.add(dependentModel);
            } else if (RFUtils.isInstance(RFPageModel.class, dependentModel)) {
                effectedPageModels.add(dependentModel);
            } else if (RFUtils.isInstance(RFFormModel.class, dependentModel)) {
                effectedModels.add(dependentModel);
            }
        }

        effectedModels.addAll(effectedPageModels);
        effectedModels.addAll(effectedSectionModels);
        effectedModels.addAll(effectedFieldModels);

        for (int i = 0; i < effectedModels.size(); i++) {
            RFBaseModel dependentModel = effectedModels.get(i);
            RFBehaviourModel behaviourModel = elementBehaviour.get(dependentModel);

            if (RFUtils.isInstance(RFElementModel.class, dependentModel) && behaviourModel.getData() != null) {
                ((RFElementModel) dependentModel).setValue(behaviourModel.getData());
            }

            if (RFUtils.isInstance(RFElementModel.class, dependentModel)) {
                setVisibilityAndEditabilityToField((RFElementModel) dependentModel, behaviourModel);
            } else if (RFUtils.isInstance(RFSectionModel.class, dependentModel)) {
                setVisibilityAndEditabilityToSection((RFSectionModel) dependentModel, behaviourModel);
            } else if (RFUtils.isInstance(RFPageModel.class, dependentModel)) {
                setVisibilityAndEditabilityToPage((RFPageModel) dependentModel, behaviourModel);
            } else if (RFUtils.isInstance(RFFormModel.class, dependentModel)) {
                setVisibilityAndEditabilityToForm((RFFormModel) dependentModel, behaviourModel);
            }
        }
    }

    /**
     * This method is used for setting the editability and visibility properties of behaviour model to field.
     *
     * @param field
     * @param behaviourModel
     */
    private void setVisibilityAndEditabilityToField(RFElementModel field, RFBehaviourModel behaviourModel) {
        field.getStyle().setEditability(behaviourModel.isEditability());
        field.getStyle().setVisibility(behaviourModel.isVisibility());
    }

    /**
     * This method is used for setting the editability and visibility properties of behaviour model to section.
     *
     * @param section
     * @param behaviourModel
     */
    private void setVisibilityAndEditabilityToSection(RFSectionModel section, RFBehaviourModel behaviourModel) {
        section.getStyle().setEditability(behaviourModel.isEditability());
        section.getStyle().setVisibility(behaviourModel.isVisibility());

        for (int i = 0; i < section.getFields().size(); i++) {
            setVisibilityAndEditabilityToField(section.getFields().get(i), behaviourModel);
        }
    }

    /**
     * This method is used for setting the editability and visibility properties of behaviour model to page.
     *
     * @param page
     * @param behaviourModel
     */
    private void setVisibilityAndEditabilityToPage(RFPageModel page, RFBehaviourModel behaviourModel) {
        page.getStyle().setEditability(behaviourModel.isEditability());
        page.getStyle().setVisibility(behaviourModel.isVisibility());

        for (int i = 0; i < page.getSections().size(); i++) {
            setVisibilityAndEditabilityToSection(page.getSections().get(i), behaviourModel);
        }
    }

    /**
     * This method is used for setting the editability and visibility properties of behaviour model to form.
     * @param form
     * @param behaviourModel
     */
    private void setVisibilityAndEditabilityToForm(RFFormModel form, RFBehaviourModel behaviourModel) {
        form.getStyle().setEditability(behaviourModel.isEditability());
        form.getStyle().setVisibility(behaviourModel.isVisibility());

        for (int i = 0; i < form.getPages().size(); i++) {
            setVisibilityAndEditabilityToPage(form.getPages().get(i), behaviourModel);
        }
    }
}
