package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegFourthModel {


    @SerializedName("height")
    double height;

    @SerializedName("age")
    double age;

    public RegFourthModel( double height, double age ) {

        this.height = height;
        this.age = age;
    }


}
