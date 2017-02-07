package com.rapidBizApps.lavasa.ParseAndRender;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Constants.RFElementTypeConstants;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.Models.RFStyleModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rapidBizApps.lavasa.Views.RFBaseElement;
import com.rba.ui.MaterialEditText;

/**
 * Created by cdara on 27-01-2016.
 */

/**
 * Use: This class is used for applying the styles to form/page/section/field
 */
public final class RFStyleRenderer {

    private Context mContext;

    public RFStyleRenderer(Context context) {
        mContext = context;
    }

    /**
     * Applies styles for all
     * If any property is not related to that particular form element we will simply ignore that property.
     *
     * @param element
     */
    public void applyStyling(RFBaseElement element) {

        View view = element.getView();
        RFStyleModel style = element.getModel().getStyle();

        if (style == null) {
            style = new RFStyleModel();
            element.getModel().setStyle(style);
        }

        // This will change the visibility of the view based on the data in the style
        if (RFUtils.getVisibility(style.getVisibility()) == View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        // TODO: 27-07-2016 Anirudh added the below if condition to hide the page title
        if(element.getModel().getId()!=null) {
            if (element.getModel().getId().equalsIgnoreCase("page_title_id")) {
                view.setVisibility(View.GONE);
            }
        }
        // this will set the editability of the view.
        // Used enable instead of editability for supporting all the views other than edit text type too.
        disableViews(view, style.isEditability());

        // This will sets the background to the views.

        view.setBackgroundColor(RFUtils.getColor(style.getBackgroundColor()));

        if (style.getBackgroundMode().length() != 0) {

        }

        if (style.getBackgroundImage().length() != 0) {

        }

        RFShapeDrawable shapeDrawable = null;

        if (style.getCornerRadius() != 0 || !style.getBorderColor().equals("(0,0,0)") || style.getBorderThickness() != 0) {
            if (style.getBorderThickness() == 0) {
                style.setBorderThickness(2);
            }

            shapeDrawable = new RFShapeDrawable((int) style.getCornerRadius(), (int) style.getBorderThickness(), RFUtils.getColor(style.getBorderColor()));
            view.setBackground(shapeDrawable);
        }

        // This wil sets the mentioned font styles to the views

        if (RFUtils.isInstance(RFElementModel.class, element.getModel())) {
            RFElementModel elementModel = (RFElementModel) element.getModel();
            switch (elementModel.getType()) {

                case RFElementTypeConstants.ETC_TEXT:
                case RFElementTypeConstants.ETC_TEXT_BOX:
                case RFElementTypeConstants.ETC_EMAIL:
                case RFElementTypeConstants.ETC_PASSWORD:
                case RFElementTypeConstants.ETC_NUMBER:
                case RFElementTypeConstants.ETC_PHONE:
                case RFElementTypeConstants.ETC_DATE:
                case RFElementTypeConstants.ETC_TIME:
                case RFElementTypeConstants.ETC_SINGLE_SELECTION:
                case RFElementTypeConstants.ETC_MULTI_SELECTION:
                case RFElementTypeConstants.ETC_NUMBER_DECIMAL:
                    setTextPropertiesToMaterialEditText(view, style);
                    break;

                case RFElementTypeConstants.ETC_CREDIT_CARD:
                    setTextPropertiesToCreditCard(view, style);
                    break;

                case RFElementTypeConstants.ETC_SIGNATURE:
                    setTextPropertiesToSignature(view, style);
                    break;

                case RFElementTypeConstants.ETC_IMAGE:
                    setTextPropertiesToImage(view, style);
                    break;

                case RFElementTypeConstants.ETC_SEEK_BAR:
                    setTextPropertiesToSeekBar(view, style);
                    break;

                case RFElementTypeConstants.ETC_IMAGE_PICKER:
                    setTextPropertiesToCaptureImage(view, style);
                    break;

                case RFElementTypeConstants.ETC_LOCATION:
                    setTextPropertiesToLocation(view, style);
                    break;

                case RFElementTypeConstants.ETC_TITLE:
                    setTextPropertiesToTextView(view, style);
                    break;

                case RFElementTypeConstants.ETC_SWITCH:
                    setTextPropertiesToSwitch(view, style);
                    break;
            }
        }
    }

    /**
     * This method is used for retrieving the typeface based on the fontName from Assets
     *
     * @param fontName
     * @return
     */
    private Typeface getTypeface(String fontName) {
        if (fontName == null || fontName.length() == 0) {
            return null;
        }

        return Typeface.createFromAsset(mContext.
                getAssets(), fontName);
    }

    /**
     * This method is used for disabling the views
     *
     * @param view
     * @param isEnabled
     */
    private void disableViews(View view, boolean isEnabled) {
        if (!RFUtils.isInstance(ViewGroup.class, view)) {
            view.setEnabled(isEnabled);
            return;
        }

        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            disableViews(((ViewGroup) view).getChildAt(i), isEnabled);
        }
    }

    /**
     * This method is used for setting text properties to material edittext
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToMaterialEditText(View view, RFStyleModel style) {
        MaterialEditText editText = (MaterialEditText) view;
        editText.setTextColor(RFUtils.getColor(style.getFontColor()));
        editText.setTextSize(style.getFontSize());

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());
        editText.setTypeface(typeface, RFUtils.getFontStyle(style.getFontStyle()));

        editText.setFloatingLabelTextColor(RFUtils.getColor(style.getTitleFontColor()));
        if (style.getTitleFontSize() != 0) {
            editText.setFloatingLabelTextSize((int) style.getTitleFontSize());
        }
        // TODO: need to add title_font_style to floating label
    }

    /**
     * This method is used for setting the text properties to text view.
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToTextView(View view, RFStyleModel style) {
        TextView textView = (TextView) view;
        textView.setTextSize(style.getFontSize());
        textView.setTextColor(RFUtils.getColor(style.getFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());
        textView.setTypeface(typeface, RFUtils.getFontStyle(style.getFontStyle()));
    }

    /**
     * This method is used for setting the text properties to switch.
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToSwitch(View view, RFStyleModel style) {
        Switch switchView = (Switch) view;
        switchView.setTextSize(style.getFontSize());
        switchView.setTextColor(RFUtils.getColor(style.getFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());
        switchView.setTypeface(typeface, RFUtils.getFontStyle(style.getFontStyle()));
    }

    /**
     * This method is used for setting text properties to credit card
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToCreditCard(View view, RFStyleModel style) {
        LinearLayout creditCardView = (LinearLayout) view;

        MaterialEditText cardNumber = (MaterialEditText) creditCardView.findViewById(R.id.credit_card_number);
        MaterialEditText cvv = (MaterialEditText) creditCardView.findViewById(R.id.cvc);
        MaterialEditText month = (MaterialEditText) creditCardView.findViewById(R.id.expiry_month);
        MaterialEditText year = (MaterialEditText) creditCardView.findViewById(R.id.expiry_year);

        setTextPropertiesToMaterialEditText(cardNumber, style);
        setTextPropertiesToMaterialEditText(cvv, style);
        setTextPropertiesToMaterialEditText(month, style);
        setTextPropertiesToMaterialEditText(year, style);
    }

    /**
     * This method is used for setting the text properties to signature
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToSignature(View view, RFStyleModel style) {
        LinearLayout signatureView = (LinearLayout) view;
        TextView signatureLabel = (TextView) signatureView.findViewById(R.id.signature_label);
        Button retry = (Button) signatureView.findViewById(R.id.signature_retry);

        if (style.getTitleFontSize() != 0) {
            signatureLabel.setTextSize((int) style.getTitleFontSize());
        }else {
            signatureLabel.setTextSize(style.getFontSize());
        }
        retry.setTextSize(style.getFontSize());

        signatureLabel.setTextColor(RFUtils.getColor(style.getTitleFontColor()));
        retry.setTextColor(RFUtils.getColor(style.getFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());

        signatureLabel.setTypeface(typeface, RFUtils.getFontStyle(style.getTitleFontStyle()));

        retry.setTypeface(typeface, RFUtils.getFontStyle(style.getFontStyle()));
    }

    /**
     * This method is used for setting text properties to image.
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToImage(View view, RFStyleModel style) {
        LinearLayout imageView = (LinearLayout) view;
        TextView imageLabel = (TextView) imageView.findViewById(R.id.image_label);

        if (style.getTitleFontSize() != 0) {
            imageLabel.setTextSize((int) style.getTitleFontSize());
        }else {
            imageLabel.setTextSize(style.getFontSize());
        }
        imageLabel.setTextColor(RFUtils.getColor(style.getTitleFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());

        imageLabel.setTypeface(typeface, RFUtils.getFontStyle(style.getTitleFontStyle()));

    }


    /**
     * This method is used for setting text properties to Seek Bar
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToSeekBar(View view, RFStyleModel style) {
        LinearLayout seekBarView = (LinearLayout) view;
        EditText seekBarValue = (EditText) seekBarView.findViewById(R.id.seek_bar_value);
        TextView seekBarLabel = (TextView) seekBarView.findViewById(R.id.seek_bar_label);

        if (style.getTitleFontSize() != 0) {
            seekBarLabel.setTextSize((int) style.getTitleFontSize());
        }else {
            seekBarLabel.setTextSize(style.getFontSize());
        }
        seekBarValue.setTextSize(style.getFontSize());

        seekBarLabel.setTextColor(RFUtils.getColor(style.getTitleFontColor()));
        seekBarValue.setTextColor(RFUtils.getColor(style.getFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());

        seekBarLabel.setTypeface(typeface, RFUtils.getFontStyle(style.getTitleFontStyle()));
        seekBarValue.setTypeface(typeface, RFUtils.getFontStyle(style.getFontStyle()));
    }

    /**
     * This method is used for setting text properties to capture image
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToCaptureImage(View view, RFStyleModel style) {
        LinearLayout captureImageView = (LinearLayout) view;
        TextView captureImageLabel = (TextView) captureImageView.findViewById(R.id.capture_image_label);

        if (style.getTitleFontSize() != 0) {
            captureImageLabel.setTextSize((int) style.getTitleFontSize());
        }else {
            captureImageLabel.setTextSize(style.getFontSize());
        }
        captureImageLabel.setTextColor(RFUtils.getColor(style.getTitleFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());

        captureImageLabel.setTypeface(typeface, RFUtils.getFontStyle(style.getTitleFontStyle()));
    }

    /**
     * This method is used for setting text properties to Location
     *
     * @param view
     * @param style
     */
    private void setTextPropertiesToLocation(View view, RFStyleModel style) {
        LinearLayout mLocationView = (LinearLayout) view;

        TextView locationLabel = (TextView) mLocationView.findViewById(R.id.location_label);
        MaterialEditText latitude = (MaterialEditText) mLocationView.findViewById(R.id.latitude);
        MaterialEditText longitude = (MaterialEditText) mLocationView.findViewById(R.id.longitude);

        if (style.getTitleFontSize() != 0) {
            locationLabel.setTextSize((int) style.getTitleFontSize());
        }else {
            locationLabel.setTextSize(style.getFontSize());
        }
        locationLabel.setTextColor(RFUtils.getColor(style.getTitleFontColor()));

        Typeface typeface = null;
        typeface = getTypeface(style.getFontName());

        locationLabel.setTypeface(typeface, RFUtils.getFontStyle(style.getTitleFontStyle()));

        setTextPropertiesToMaterialEditText(latitude, style);
        setTextPropertiesToMaterialEditText(longitude, style);
    }
}
