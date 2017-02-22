package com.almabranding.yummi.models;

/**
 * Created by ioshero on 29/07/16.
 */
public class SettingsModel {


    public String name = "";
    public String value1 = "";
    public String value2 = "";
    public boolean selected2 = false;

    public String getName() {
        return name;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public boolean isSelected2() {
        return selected2;
    }

    public void setSelected2(boolean selected2) {
        this.selected2 = selected2;
    }


    public SettingsModel(String name, String value1, String value2, boolean isSelected2){

        this.value1 = value1;
        this.value2 = value2;
        this.name = name;
        this.selected2 = isSelected2;


    }
}
