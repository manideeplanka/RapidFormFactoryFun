package com.rapidBizApps.lavasa.ParseAndRender;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

/**
 * Created by kkalluri on 2/8/2016.
 */

/**
 * Use: This class is used as rectangular shaped background for the form/page/section/field.
 */
public final class RFShapeDrawable extends GradientDrawable {


    public RFShapeDrawable(int cornerRadius, int pStrokeWidth, int pStrokeColor) {
        super();
        setStroke(pStrokeWidth, pStrokeColor);
        setShape(GradientDrawable.RECTANGLE);
        setCornerRadius(cornerRadius);
        setColor(Color.WHITE);

    }

}
