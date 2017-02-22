package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegSecondModel {



    @SerializedName("stageName")
    String stageName;

    @SerializedName("realName")
    String realName;

    public RegSecondModel( String stageName,  String realName ) {

        if (stageName == null)
            stageName = "";
        if (realName == null)
            realName = "";

        this.stageName = stageName;
        this.realName = realName;
    }


}
