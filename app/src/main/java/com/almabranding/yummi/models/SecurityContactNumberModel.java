package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 03/06/16.
 */
public class SecurityContactNumberModel {

    @SerializedName("number")
    String number;

    public void setSecurityNumber(final String securityNumber) {
        this.number = securityNumber;
    }

    public SecurityContactNumberModel(final String securityNumber){
        this.number = securityNumber;
    }
}
