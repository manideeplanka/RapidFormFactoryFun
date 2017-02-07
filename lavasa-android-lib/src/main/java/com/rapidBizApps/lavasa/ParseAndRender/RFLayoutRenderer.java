package com.rapidBizApps.lavasa.ParseAndRender;

import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Constants.RFLayoutConstants;
import com.rapidBizApps.lavasa.Models.RFBaseModel;
import com.rapidBizApps.lavasa.Models.RFLayoutModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rapidBizApps.lavasa.Views.RFBaseElement;
import com.rba.ui.MaterialEditText;

/**
 * Created by cdara on 27-01-2016.
 */

/**
 * This is used for applying layouts to form/page/field/section
 */
public final class RFLayoutRenderer {

    /**
     * Applies layout for all. For the form elements default alignment is center. The alignment which is specified in the json is applicable to
     * edit text type elements. For the edit text type elements the alignment is related to the text.
     * If any property is not related to that particular form element we will simply ignore that property.
     *
     * @param element
     */
    public void applyLayout(RFBaseElement element) {

        View view = element.getView();
        RFLayoutModel layout = element.getModel().getLayout();

        setMarginsAndPadding(view, element.getModel());
        setGravity(view, layout);

    }

    /**
     * This will set margins and padding to view based on their parent layout.
     *
     * @param view
     * @param model
     */
    private void setMarginsAndPadding(View view, RFBaseModel model) {

        RFLayoutModel layout = model.getLayout();
        View parentView = (View) view.getParent();

        // TODO: Need to handle layout params for remaining types too
        if (parentView != null && RFUtils.isInstance(LinearLayout.class, parentView)) {
            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams((int) layout.getWidth(), (int) layout.getHeight());

            viewParams.leftMargin = (int) layout.getLeftMargin();
            viewParams.rightMargin = (int) layout.getRightMargin();
            viewParams.topMargin = (int) layout.getTopMargin();
            viewParams.bottomMargin = (int) layout.getBottomMargin();

            view.setLayoutParams(viewParams);

            // Material edit text is not supporting the setPadding method.
            if (RFUtils.isInstance(MaterialEditText.class, view)) {
                ((MaterialEditText) view).setPaddings((int) layout.getLeftPadding(), (int) layout.getTopPadding(), (int) layout.getRightPadding(), (int) layout.getBottomPadding());
            } else {
                view.setPadding((int) layout.getLeftPadding(), (int) layout.getTopPadding(), (int) layout.getRightPadding(), (int) layout.getBottomPadding());
            }

        } else if (parentView != null && RFUtils.isInstance(RelativeLayout.class, parentView)) {
            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams((int) layout.getWidth(), (int) layout.getHeight());

            viewParams.leftMargin = (int) layout.getLeftMargin();
            viewParams.rightMargin = (int) layout.getRightMargin();
            viewParams.topMargin = (int) layout.getTopMargin();
            viewParams.bottomMargin = (int) layout.getBottomMargin();

            view.setLayoutParams(viewParams);

            // Material edit text is not supporting the setPadding method.
            if (RFUtils.isInstance(MaterialEditText.class, view)) {
                ((MaterialEditText) view).setPaddings((int) layout.getLeftPadding(), (int) layout.getTopPadding(), (int) layout.getRightPadding(), (int) layout.getBottomPadding());
            } else {
                view.setPadding((int) layout.getLeftPadding(), (int) layout.getTopPadding(), (int) layout.getRightPadding(), (int) layout.getBottomPadding());
            }

        } else if (parentView != null && RFUtils.isInstance(ViewPager.class, parentView) && RFUtils.isInstance(RFPageModel.class, model)) {
            // Here we cannot apply margin to we pager directly so, I am applying marigns to the recyler view which is page.
            View pageView = ((LinearLayout) view).findViewById(R.id.page_view);

            if (pageView != null) {
                view.setPadding((int) layout.getLeftPadding(), (int) layout.getTopPadding(), (int) layout.getRightPadding(), (int) layout.getBottomPadding());
            }
        }
    }

    /**
     * This method will set gravity to view
     *
     * @param view
     * @param layout
     */
    private void setGravity(View view, RFLayoutModel layout) {
        // This will sets alignment to the view.
        int gravity = 0;
        String alignment = layout.getAlignment();

        switch (alignment) {
            case RFLayoutConstants.LC_LEFT:
                gravity = Gravity.LEFT;
                break;
            case RFLayoutConstants.LC_RIGHT:
                gravity = Gravity.RIGHT;
                break;
            case RFLayoutConstants.LC_TOP:
                gravity = Gravity.TOP;
                break;
            case RFLayoutConstants.LC_BOTTOM:
                gravity = Gravity.BOTTOM;
                break;
            case RFLayoutConstants.LC_CENTER:
                gravity = Gravity.CENTER;
                break;
        }

        if (view instanceof MaterialEditText) {
            ((MaterialEditText) view).setGravity(gravity);
        } else if (view instanceof TextView) {
            ((TextView) view).setGravity(gravity);
        } else if (view instanceof EditText) {
            ((EditText) view).setGravity(gravity);
        }

    }
}
