package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 30/06/16.
 */
public class Logout_model {

    @SerializedName("deviceToken")
    String deviceToken;

    public Logout_model(final String deviceToken){
        this.deviceToken = deviceToken;
    }

}
