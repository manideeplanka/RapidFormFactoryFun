package com.rapidBizApps.lavasa.Models;

import com.rapidBizApps.lavasa.Constants.RFStyleConstants;

/**
 * Created by cdara on 20-01-2016.
 */
public class RFStyleModel {
    private String backgroundColor;

    // background mode can be fit_start/fit_end/fit_xy
    private String backgroundMode;

    private String backgroundImage;
    private String borderColor;
    private float borderThickness;
    private float cornerRadius;
    private float fontSize;
    private String fontStyle;
    private String fontColor;
    private float titleFontSize;
    private String titleFontStyle;
    private String titleFontColor;

    // Font name means typeface
    private String fontName;

    private boolean visibility = true;
    private boolean editability = true;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundMode() {
        return backgroundMode;
    }

    public void setBackgroundMode(String backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public float getBorderThickness() {
        return borderThickness;
    }

    public void setBorderThickness(float borderThickness) {
        this.borderThickness = borderThickness;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public float getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(float titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public String getTitleFontStyle() {
        return titleFontStyle;
    }

    public void setTitleFontStyle(String titleFontStyle) {
        this.titleFontStyle = titleFontStyle;
    }

    public String getTitleFontColor() {
        return titleFontColor;
    }

    public void setTitleFontColor(String titleFontColor) {
        this.titleFontColor = titleFontColor;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isEditability() {
        return editability;
    }

    public void setEditability(boolean editability) {
        this.editability = editability;
    }

    // TODO: Need to discuss these methods with team
    public void setDefaultFieldStyleProperties(RFElementModel model) {

        // If Id is null then the field is the title of section/page
        if (model.getId() == null) {
            fontStyle = RFStyleConstants.SC_NORMAL;
        } else {
            fontStyle = RFStyleConstants.SC_NORMAL;
        }

        backgroundColor = "(255,255,255)";
        backgroundMode = "";
        backgroundImage = "";
        borderColor = "(0,0,0)";
        borderThickness = 0;
        cornerRadius = 0;
        fontSize = 14;
        fontColor = "(0,0,0)";
        titleFontSize = 0;
        titleFontStyle = RFStyleConstants.SC_NORMAL;
        titleFontColor = "(66,135,218)";
        fontName = null;
        visibility = true;
        editability = true;
    }

    // TODO: Need to discuss these methods with team
    public void setDefaultSectionStyleProperties() {
        backgroundColor = "(255,255,255)";
        backgroundMode = "";
        backgroundImage = "";
        borderColor = "(220,220,220)";
        borderThickness = 1;
        cornerRadius = 0;
        fontSize = 16;
        fontStyle = RFStyleConstants.SC_NORMAL;
        fontColor = "(0,0,0)";
        titleFontSize = 0;
        titleFontStyle = RFStyleConstants.SC_NORMAL;
        titleFontColor = "(0,0,0)";
        fontName = null;
        visibility = true;
        editability = true;
    }

    // TODO: Need to discuss these methods with team
    public void setDefaultPageStyleProperties() {
        backgroundColor = "(248,248,248)";
        backgroundMode = "";
        backgroundImage = "";
        borderColor = "(0,0,0)";
        borderThickness = 0;
        cornerRadius = 0;
        fontSize = 0;
        fontStyle = RFStyleConstants.SC_NORMAL;
        fontColor = "(0,0,0)";
        titleFontSize = 18;
        titleFontStyle = RFStyleConstants.SC_NORMAL;
        titleFontColor = "(0,0,0)";
        fontName = null;
        visibility = true;
        editability = true;
    }

    // TODO: Need to discuss these methods with team
    public void setDefaultFormStyleProperties() {
        backgroundColor = "(255,255,255)";
        backgroundMode = "";
        backgroundImage = "";
        borderColor = "(0,0,0)";
        borderThickness = 0;
        cornerRadius = 0;
        fontSize = 18;
        fontStyle = RFStyleConstants.SC_NORMAL;
        fontColor = "(0,0,0)";
        titleFontSize = 0;
        titleFontStyle = RFStyleConstants.SC_NORMAL;
        titleFontColor = "(0,0,0)";
        fontName = null;
        visibility = true;
        editability = true;
    }
}
