package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegFifthModel {


    @SerializedName("hairColor")
    String hairColor;

    @SerializedName("bodyType")
    String bodyType;

    @SerializedName("bust")
    String bust;

    public RegFifthModel(String hairColor, String bodyType, String bust ) {

        if (hairColor == null)
            hairColor = "";
        if (bodyType == null)
            bodyType = "";

        if (bust == null)
            bust = "";

        this.hairColor = hairColor;
        this.bodyType = bodyType;
        this.bust = bust;
    }


}
