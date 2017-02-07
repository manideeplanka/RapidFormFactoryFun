package com.rapidBizApps.lavasa.ParseAndRender;

import android.content.Context;

import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;
import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Exceptions.RFRegisterException;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rapidBizApps.lavasa.Views.RFCustomField;
import com.rapidBizApps.lavasa.Views.RFDatePicker;
import com.rapidBizApps.lavasa.Views.RFEditText;
import com.rapidBizApps.lavasa.Views.RFMultiSelect;
import com.rapidBizApps.lavasa.Views.RFSingleSelect;
import com.rapidBizApps.lavasa.Views.RFSwitch;
import com.rapidBizApps.lavasa.Views.RFTextView;
import com.rapidBizApps.lavasa.Views.RFTimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by cdara on 27-01-2016.
 */

/**
 * Use: This used for storing the registered fields/elements information
 */
public final class RFRegister {

    // registeredElements(type,class) - type can be custom type or default type
    // class - CustomElement class or CustomField class
    private HashMap<String, Class> mRegisteredViews;
    private Context mContext;

    public RFRegister(RFController controller) {
        // This will not allow object creation if the controller is null
        if (controller == null) {
            throw new NullPointerException();
        }

        mRegisteredViews = new HashMap<>();
        mContext = controller.getContext();
    }

    /**
     * This method is used for mapping custom field to a  particular type.
     * If the user calls the register field more than once, if there are any duplicates previous values will be replaced with new ones.
     * We should throw exception if the given custom class doesn't extend RFCustomField.
     */
    public void registerFields(HashMap<String, Class> customFields) {

        Iterator customFieldsIterator = customFields.entrySet().iterator();
        while (customFieldsIterator.hasNext()) {

            Map.Entry<String, Class> customFieldsMap = (Map.Entry<String, Class>) customFieldsIterator.next();
            String type = customFieldsMap.getKey();
            Class customFieldType = customFieldsMap.getValue();

            if (type == null || customFieldType == null) {
                continue;
            }

            Class currentClass = customFieldType;

            while (currentClass.getSuperclass() != null) {
                String className = currentClass.getSuperclass().getName();
                if (className.equals(RFCustomField.class.getName())) {
                    // If that type already exists this if will remove that type.
                    if (mRegisteredViews.containsKey(type)) {
                        mRegisteredViews.remove(type);
                    }
                    mRegisteredViews.put(type, customFieldType);
                    break;
                }
                currentClass = currentClass.getSuperclass();
            }

            if (currentClass.getSuperclass() == null) {
                throw new RFRegisterException(RFUtils.getString(mContext, R.string.register_field_error));
            }
        }
    }

    /**
     * This method is used for mapping custom element to a  particular type.
     * If the user calls the registerElements more than once, if there are any duplicates previous values will be replaced with new ones.
     * We should throw exception if the given custom class doesn't extend default elements.
     *
     * @param customElements
     * @return
     */
    public void registerElements(ArrayList<Class> customElements) {

        for (int i = 0; i < customElements.size(); i++) {

            Class customElementType = customElements.get(i);
            if (customElementType == null) {
                continue;
            }

            Class currentClass = customElementType;
            String defaultType = null;

            while (currentClass.getSuperclass() != null) {
                String className = currentClass.getSuperclass().getName();

                if (className.equals(RFEditText.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_TEXT;
                    break;
                } else if (className.equals(RFDatePicker.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_DATE;
                    break;
                } else if (className.equals(RFMultiSelect.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_MULTI_SELECTION;
                    break;
                } else if (className.equals(RFSingleSelect.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_SINGLE_SELECTION;
                    break;
                } else if (className.equals(RFSwitch.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_SWITCH;
                    break;
                } else if (className.equals(RFTextView.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_TITLE;
                    break;
                } else if (className.equals(RFTimePicker.class.getName())) {
                    defaultType = RFElementTypeConstants.ETC_TIME;
                    break;
                }

                currentClass = currentClass.getSuperclass();
            }


            if (currentClass.getSuperclass() == null) {
                throw new RFRegisterException(RFUtils.getString(mContext, R.string.register_element_error));
            } else {
                if (mRegisteredViews.containsKey(customElementType)) {
                    mRegisteredViews.remove(customElementType);
                }
                mRegisteredViews.put(defaultType, customElementType);
            }
        }
    }

    /**
     * This will check whether the specified type has any registered class.
     *
     * @param type
     * @return
     */
    public boolean checkForRegisteredElement(String type) {
        return mRegisteredViews.containsKey(type);
    }


    /**
     * This  method will be called while rendering the view
     * Returns registered class otherwise null
     *
     * @param type
     * @return
     */
    public Class getViewClass(String type) {
        return mRegisteredViews.get(type);
    }
}
