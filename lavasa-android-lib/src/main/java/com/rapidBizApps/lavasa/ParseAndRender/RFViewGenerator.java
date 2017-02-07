package com.rapidBizApps.lavasa.ParseAndRender;

import android.view.View;

import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Models.RFPagesStates;

/**
 * Created by cdara on 25-01-2016.
 */

/**
 * Use: This is used for passing the calls related to views from controller to container
 */
public final class RFViewGenerator {
    private RFContainer mContainer;
    private RFController mController;

    public RFViewGenerator(RFController controller) {
        // This will not allow object creation if the controller is null
        if (controller == null) {
            throw new NullPointerException();
        }

        mController = controller;
    }

    /**
     * This will invokes the container method from to create view pager
     *
     * @return
     */
    public void createFormContainer(View formView) {
        mContainer = new RFContainer(mController);
        mContainer.createFormContainer(formView);
    }

    /**
     * Used for navigation to particular field/section/page
     *
     * @param keyName keyName can be pageKeyName/sectionKeyName/fieldKeyName
     * @return
     */
    public void navigateTo(String keyName) {
        mContainer.navigateTo(keyName);
    }

    /**
     * @return returns the state of the pae after navigation
     */
    public RFPagesStates moveToNextPage() {
        return mContainer.moveToNextPage();
    }

    /**
     * @return returns the state of the pae after navigation
     */
    public RFPagesStates moveToPreviousPage() {
        return mContainer.moveToPreviousPage();
    }

    public void applyValidations()
    {
        mContainer.applyValidations();
    }

}
