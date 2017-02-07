package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Constants.RFLayoutConstants;
import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Listeners.RFElementEventListenerClass;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;
import com.rapidBizApps.lavasa.Models.RFBaseModel;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFLayoutRenderer;
import com.rapidBizApps.lavasa.ParseAndRender.RFStyleRenderer;
import com.rapidBizApps.lavasa.RFUtils;

/**
 * Created by cdara on 03-02-2016.
 */

/**
 * Use: This class will be extended by all the defaults elements/custom fields
 */
// TODO: Need to discuss method names for setting the data to the model and getting data from model.
public abstract class RFBaseElement {

    private static View sFormContainer;
    private static ViewGroup sPagesContainer;
    private static LinearLayout sSectionsContainer;
    private static LinearLayout sFieldsContainer;

    private final int TABLET_MAX_COLUMNS = 2;
    private RFBaseModel mModel;
    private View mView;
    private RFStyleRenderer mStyleRenderer;
    private RFLayoutRenderer mLayoutRenderer;
    private Context mContext;
    private RFElementEventListener mElementEventListener;
    private RFElementEventListenerClass mElementEventListenerClass;

    public RFBaseElement(Context context, RFBaseModel model) {
        mContext = context;
        mModel = model;
        mElementEventListenerClass = new RFElementEventListenerClass(context, this);
        setEventListeners(mElementEventListenerClass);
        mStyleRenderer = new RFStyleRenderer(mContext);
        mLayoutRenderer = new RFLayoutRenderer();
    }

    public void setFormContainer(View formContainer) {
        sFormContainer = formContainer;
    }

    public void setPagesContainer(ViewGroup pagesContainer) {
        sPagesContainer = pagesContainer;
    }

    public void setSectionsContainer(LinearLayout sectionsContainer) {
        sSectionsContainer = sectionsContainer;
    }

    public void setFieldsContainer(LinearLayout fieldsConatiner) {
        sFieldsContainer = fieldsConatiner;
    }

    /**
     * This method will retrieves the data from the view. If any custom field is present we don't know what is present in the custom field. In this scenario
     * from the library we will just call getResponseData method in the custom field class. For maintaining the same flow we are implementing this method in the default elements too.
     *
     * @return
     */
    public abstract String getData();

    /**
     * This method will sets data to the view. If any custom field is present we don't know what is present in the custom field. In this scenario
     * from the library we will just call setData method in the custom field class. For maintaining the same flow we are implementing this method in the default elements too.
     */
    protected abstract void setData(String data);

    public abstract void setError(String error);

    /**
     * This method will set data to the model.
     *
     * @param data
     */
    public final void setDataToModel(String data) {
        if (mModel != null && RFUtils.isInstance(RFElementModel.class, mModel)) {
            ((RFElementModel) mModel).setValue(data);
        }
    }

    /**
     * This method will get data from model.
     *
     * @return
     */
    public final String getDataFromModel() {
        if (mModel != null && RFUtils.isInstance(RFElementModel.class, mModel)) {
            return ((RFElementModel) mModel).getValue();
        }

        return null;
    }

    // This will set element event listeners to the view.
    private void setEventListeners(RFElementEventListener listener) {
        mElementEventListener = listener;
    }

    public RFElementEventListener getElementListeners() {
        return mElementEventListener;
    }

    /**
     * In the setUp method  layout and styling is applied and the resultant view is added to its parent view.
     *
     * @param view
     */
    protected void setUp(View view) {
        mView = view;

        applyStyling(this);
        addViewToParent();
        applyLayout(this);

    }

    /**
     * This will adds the view to its parent.
     * If the view is field, first it sets the error and data to the view and then it adds to the parent view.
     */
    private void addViewToParent() {

        //setting id as tag, it will be helpful for retrieving the view.
        mView.setTag(mModel.getId());

        if (RFUtils.isInstance(RFPageModel.class, mModel)) {
            sPagesContainer.addView(mView);
        } else if (RFUtils.isInstance(RFTextView.class, this) && ((RFTextView) this).getTitleType().equals(RFConstants.FC_PAGE_TITLE)) {
            sSectionsContainer.addView(mView, 0);
        } else if (RFUtils.isInstance(RFSectionModel.class, mModel)) {
            if (((RFSectionModel) mModel).getFields().size() != 0) {
                sSectionsContainer.addView(mView);
            }
        } else if (RFUtils.isInstance(RFTextView.class, this) && ((RFTextView) this).getTitleType().equals(RFConstants.FC_SECTION_TITLE)) {
            sFieldsContainer.addView(mView, 0);
        } else if (RFUtils.isInstance(RFElementModel.class, mModel)) {
            setDataToView();
            this.setError(((RFElementModel) mModel).getErrorMessage());

            // This will check whether the device is tablet or not. If it is tablet it will check the orientation.
            // If the orientation is landscape it will allow
            if (RFUtils.isTablet(mContext) && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                // This will check whether the user wants to adjust columns for this particular view.
                // If the user is not willing to adjust columns for this view. It will add directly to section container
                if (!mModel.getLayout().isAdjustColumns()) {
                    sFieldsContainer.addView(this.mView);
                    return;
                }

                LinearLayout horizontalLayout;
                // This will check whether the section container contains elements or not
                // If it contains it will check the last element in the section container is horizontal layout or not.
                // If it is layout it will check whether the layout is added by rFF or user created layout
                if (sFieldsContainer != null &&
                        sFieldsContainer.getChildCount() > 0
                        && RFUtils.isInstance(LinearLayout.class, sFieldsContainer.getChildAt(sFieldsContainer.getChildCount() - 1))
                        && sFieldsContainer.getChildAt(sFieldsContainer.getChildCount() - 1).getTag() != null
                        && sFieldsContainer.getChildAt(sFieldsContainer.getChildCount() - 1).getTag().equals(RFLayoutConstants.LC_TABLET_LAYOUT)
                        ) {
                    horizontalLayout = (LinearLayout) sFieldsContainer.getChildAt(sFieldsContainer.getChildCount() - 1);

                    // It will check whether it contains maximum number of children as per tablet cols
                    if (horizontalLayout.getChildCount() < TABLET_MAX_COLUMNS) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, (int) mModel.getLayout().getHeight());
                        params.weight = 1;

                        // If the horizontal layout contains only 1 child. It will change the layout params of that layout
                        // because the first child won't contain weight 1 and width 0
                        if (horizontalLayout.getChildCount() == 1) {
                            View view = horizontalLayout.getChildAt(0);
                            view.setLayoutParams(params);
                        }

                        mView.setLayoutParams(params);

                        horizontalLayout.addView(mView);
                    } else {
                        horizontalLayout = new LinearLayout(mContext);
                        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                        horizontalLayout.setTag(RFLayoutConstants.LC_TABLET_LAYOUT);
                        horizontalLayout.addView(mView);
                        sFieldsContainer.addView(horizontalLayout);
                    }
                } else {
                    horizontalLayout = new LinearLayout(mContext);
                    horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    horizontalLayout.setTag(RFLayoutConstants.LC_TABLET_LAYOUT);
                    horizontalLayout.addView(mView);
                    sFieldsContainer.addView(horizontalLayout);
                }
            } else {
                sFieldsContainer.addView(mView);
            }
        } else if (RFUtils.isInstance(RFFormModel.class, mModel)) {
            if (RFUtils.isInstance(LinearLayout.class, sFormContainer)) {
                ((LinearLayout) sFormContainer).addView(mView);
            } else if (RFUtils.isInstance(RelativeLayout.class, sFormContainer)) {
                ((RelativeLayout) sFormContainer).addView(mView);
            }
        }
    }

    // This will sets the data retrieved from model to the view.
    private void setDataToView() {
        if (((RFElementModel) mModel).getValue() != null) {
            this.setData(((RFElementModel) mModel).getValue());
        }
    }

    // This will returns the view
    // TODO: Need to move this method to RFPage. We don't need this in other views. (Android use)
    public View getView() {
        return mView;
    }

    // This will returns the model.
    public RFBaseModel getModel() {
        return mModel;
    }

    /**
     * Applies styles for all
     *
     * @param element
     */
    private void applyStyling(RFBaseElement element) {
        mStyleRenderer.applyStyling(element);
    }

    /**
     * Applies layout for all
     *
     * @param element
     */
    private void applyLayout(RFBaseElement element) {
        mLayoutRenderer.applyLayout(element);
    }

    /**
     * Sets form listener
     *
     * @param formListener
     */
    public void setFormListener(RFFormListener formListener) {
        mElementEventListenerClass.setFormListener(formListener);
    }

    public void setRFView(RFForm rfView) {
        mElementEventListenerClass.setRFView(rfView);
    }

}
