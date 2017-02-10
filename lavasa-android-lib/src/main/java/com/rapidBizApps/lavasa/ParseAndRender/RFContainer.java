package com.rapidBizApps.lavasa.ParseAndRender;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFPagesStates;
import com.rapidBizApps.lavasa.Views.RFForm;
import com.rapidBizApps.lavasa.Views.RFPage;

import java.util.HashMap;

/**
 * Created by chandana on 09-12-2015.
 */

/**
 * Use: This will manage the pages of the form.
 */
public final class RFContainer extends PagerAdapter {

    private RFFormModel mForm;
    private RFController mController;
    private RFForm mFormView;
    private HashMap<Integer, RFPage> mPageViews;


    public RFContainer(RFController controller) {
        mController = controller;
        mForm = controller.getForm();
        mPageViews = new HashMap<>();
    }

    @Override
    public int getCount() {
        return mForm.getPages().size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (mFormView != null) {
            mFormView.setPagesContainer(container);
        }

        RFPage pageView;

        pageView = new RFPage(mController, mForm.getPages().get(position));

        mPageViews.put(position, pageView);

        return pageView.getView();

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    // for removing screenOffsetPageLimit
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public RFPage getPageView(int pagePosition) {
        return mPageViews.get(pagePosition);
    }


    /**
     * This will creates the view pager.
     *
     * @param formView
     */
    protected void createFormContainer(View formView) {
        mFormView = new RFForm(mController, mForm, formView, this);
        mFormView.setFormListener(mController.getFormListener());
        mFormView.setRFView(mFormView);
    }

    /**
     * Used for navigation to particular field/section/page
     *
     * @param keyName keyName can be pageKeyName/sectionKeyName/fieldKeyName
     * @return
     */
    protected void navigateTo(String keyName) {
        mFormView.navigateTo(keyName);
    }

    /**
     * This will navigates the form to next page from current page.
     *
     * @return page state  - returns the page state of the form.
     */
    protected RFPagesStates moveToNextPage() {
        return mFormView.moveToNextPage();
    }

    /**
     * This will navigates the form to prev page from current page
     *
     * @return page state  - returns the page state of the form.
     */
    protected RFPagesStates moveToPreviousPage() {
        return mFormView.moveToPreviousPage();
    }


    protected void applyValidations()
    {
        mFormView.applyValidations();
    }

}

