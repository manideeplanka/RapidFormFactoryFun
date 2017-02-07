package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;
import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Exceptions.RFCustomViewException;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFRegister;
import com.rapidBizApps.lavasa.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by cdara on 03-02-2016.
 */
public class RFSection extends RFBaseElement {

    private LinearLayout mSectionView;
    private RFSectionModel mSectionModel;
    private Context mContext;
    private RFRegister mRegister;

    public RFSection(RFController controller, RFSectionModel sectionModel) {
        super(controller.getContext(), sectionModel);
        mContext = controller.getContext();
        mRegister = controller.getRegister();
        setSectionModel(sectionModel);
        init();
    }

    // All the initializations for the section will be present in the init() method
    // setUp() will be called in the init method
    private void init() {

        LinearLayout sectionLayout = new LinearLayout(mContext);
        sectionLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sectionLayout.setOrientation(LinearLayout.VERTICAL);
        setSectionView(sectionLayout);

        setUp(mSectionView);
        setFieldsContainer(sectionLayout);
        if (!mSectionModel.getId().contains(RFConstants.FC_DEFAULT_SECTION)) {

            setFieldsContainer(sectionLayout);
            new RFTextView(mContext, mSectionModel.getSectionTitleField(), RFConstants.FC_SECTION_TITLE);
        }


        ArrayList<RFElementModel> fields = mSectionModel.getFields();

        // all the fields in this section are added to the section view.
        for (int i = 0; i < fields.size(); i++) {
            RFElementModel field = fields.get(i);
            String fieldType = field.getType();

            if (mRegister != null && mRegister.checkForRegisteredElement(field.getType())) {
                Class custom = mRegister.getViewClass(fieldType);
                createInstanceOfCustomClass(custom, field);
                continue;
            }

            switch (fieldType) {
                case RFElementTypeConstants.ETC_TEXT:
                    RFEditText editText = new RFEditText(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_PHONE:
                    RFPhone phone = new RFPhone(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_NUMBER:
                    RFNumber number = new RFNumber(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_PASSWORD:
                    RFPassword password = new RFPassword(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_EMAIL:
                    RFEmail email = new RFEmail(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_TEXT_BOX:
                    RFTextBox textBox = new RFTextBox(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_CREDIT_CARD:
                    RFCreditCard creditCard = new RFCreditCard(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_DATE:
                    RFDatePicker datePicker = new RFDatePicker(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_TIME:
                    RFTimePicker timePicker = new RFTimePicker(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_TITLE:
                    RFTextView textView = new RFTextView(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_SINGLE_SELECTION:
                    RFSingleSelect singleSelect = new RFSingleSelect(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_MULTI_SELECTION:
                    RFMultiSelect multiSelect = new RFMultiSelect(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_SWITCH:
                    RFSwitch switchView = new RFSwitch(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_SIGNATURE:
                    RFSignature signatureView = new RFSignature(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_IMAGE:
                    RFImageView imageView = new RFImageView(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_SEEK_BAR:
                    RFSeekBar sekBarView = new RFSeekBar(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_IMAGE_PICKER:
                    RFCaptureImage captureImageView = new RFCaptureImage(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_LOCATION:
                    RFLocation locationView = new RFLocation(mContext, field);
                    break;
                case RFElementTypeConstants.ETC_NUMBER_DECIMAL:
                    RFNumberDecimal numberDecimalView = new RFNumberDecimal(mContext, field);
                    break;
            }

            if (RFForm.sPendingNavigationKey != null && RFForm.sPendingNavigationKey.equals(field.getId())) {
                View view = sectionLayout.findViewWithTag(RFForm.sPendingNavigationKey);
                view.requestFocus();
                RFForm.sPendingNavigationKey = null;
            }
        }
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    protected void setData(String data) {

    }

    @Override
    public void setError(String error) {

    }

    private void setSectionModel(RFSectionModel sectionModel) {
        mSectionModel = sectionModel;
    }

    private void setSectionView(LinearLayout sectionView) {
        mSectionView = sectionView;
    }

    /**
     * This will verify the constructor signatures and creates instances.
     *
     * @param custom
     */
    private void createInstanceOfCustomClass(Class custom, RFElementModel field) {
        Object[] arguments = null;
        boolean isConstructorExists = false;

        try {
            Constructor customClassConstructor = custom.getDeclaredConstructor();
            customClassConstructor.setAccessible(true);
            if (customClassConstructor != null) {
                isConstructorExists = true;
                customClassConstructor.newInstance();
            }

        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
            throw new RFCustomViewException(e);
        } catch (InvocationTargetException e) {
            throw new RFCustomViewException(e);
        } catch (InstantiationException e) {
            throw new RFCustomViewException(e);
        }

        try {
            Constructor customClassConstructor = custom.getDeclaredConstructor(Context.class);
            customClassConstructor.setAccessible(true);
            if (customClassConstructor != null) {
                isConstructorExists = true;
                arguments = new Object[1];
                arguments[0] = mContext;
                customClassConstructor.newInstance(arguments);
            }

        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
            throw new RFCustomViewException(e);
        } catch (InvocationTargetException e) {
            throw new RFCustomViewException(e);
        } catch (InstantiationException e) {
            throw new RFCustomViewException(e);
        }

        try {
            Constructor customClassConstructor = custom.getDeclaredConstructor(Context.class, RFElementModel.class);
            customClassConstructor.setAccessible(true);
            if (customClassConstructor != null) {
                isConstructorExists = true;
                arguments = new Object[2];
                arguments[0] = mContext;
                arguments[1] = field;
                Object customClassInstance = customClassConstructor.newInstance(arguments);
            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
            throw new RFCustomViewException(e);
        } catch (InvocationTargetException e) {
            throw new RFCustomViewException(e);
        } catch (InstantiationException e) {
            throw new RFCustomViewException(e);
        }

        if (!isConstructorExists) {
            throw new RFCustomViewException(mContext.getString(R.string.no_constructor_exception));
        }
    }

}


