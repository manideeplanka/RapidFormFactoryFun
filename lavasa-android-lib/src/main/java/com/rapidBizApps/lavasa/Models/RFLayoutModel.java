package com.rapidBizApps.lavasa.Models;

import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Constants.RFLayoutConstants;

/**
 * Created by cdara on 20-01-2016.
 */

/**
 * Use: Layout information is stored in this model
 */
public final class RFLayoutModel {

    private boolean adjustColumns = true;
    private float width = LinearLayout.LayoutParams.MATCH_PARENT;
    private float height = LinearLayout.LayoutParams.WRAP_CONTENT;
    private String alignment = RFLayoutConstants.LC_LEFT;

    private float leftPadding = 5;
    private float rightPadding = 5;
    private float topPadding = 5;
    private float bottomPadding = 5;
    private float leftMargin = 16;
    private float rightMargin = 16;
    private float topMargin = 16;
    private float bottomMargin = 16;

    public boolean isAdjustColumns() {
        return adjustColumns;
    }

    public void setAdjustColumns(boolean adjustColumns) {
        this.adjustColumns = adjustColumns;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public float getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(float leftPadding) {
        this.leftPadding = leftPadding;
    }

    public float getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(float rightPadding) {
        this.rightPadding = rightPadding;
    }

    public float getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(float topPadding) {
        this.topPadding = topPadding;
    }

    public float getBottomPadding() {
        return bottomPadding;
    }

    public void setBottomPadding(float bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public float getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(float leftMargin) {
        this.leftMargin = leftMargin;
    }

    public float getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(float rightMargin) {
        this.rightMargin = rightMargin;
    }

    public float getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(float topMargin) {
        this.topMargin = topMargin;
    }

    public float getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(float bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    // TODO: Need to discuss
    public void setDefaultFieldLayoutProperties(RFElementModel model) {
        adjustColumns = true;
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // If Id is null then the field is the title of section/page
        if (model.getId() == null) {
            alignment = RFLayoutConstants.LC_LEFT;
        } else {
            alignment = RFLayoutConstants.LC_LEFT;
        }

        leftPadding = 16;
        rightPadding = 16;
        topPadding = 16;
        bottomPadding = 16;
        leftMargin = 48;
        rightMargin = 48;
        topMargin = 0;
        bottomMargin = 0;
    }

    // TODO: Need to discuss
    public void setDefaultSectionLayoutProperties() {
        adjustColumns = true;
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;
        alignment = RFLayoutConstants.LC_LEFT;
        leftPadding = 1;
        rightPadding = 1;
        topPadding = 3;
        bottomPadding = 3;
        leftMargin = 64;
        rightMargin = 64;
        topMargin = 40;
        bottomMargin = 0;
    }

    // TODO: Need to discuss
    public void setDefaultPageLayoutProperties() {
        adjustColumns = true;
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;
        alignment = RFLayoutConstants.LC_LEFT;
        leftPadding = 0;
        rightPadding = 0;
        topPadding = 0;
        bottomPadding = 0;
        leftMargin = 0;
        rightMargin = 0;
        topMargin = 0;
        bottomMargin = 0;
    }

    // TODO: Need to discuss
    public void setDefaultFormLayoutProperties() {
        adjustColumns = true;
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.WRAP_CONTENT;
        alignment = RFLayoutConstants.LC_CENTER;
        leftPadding = 0;
        rightPadding = 0;
        topPadding = 0;
        bottomPadding = 0;
        leftMargin = 0;
        rightMargin = 0;
        topMargin = 0;
        bottomMargin = 0;
    }
}
