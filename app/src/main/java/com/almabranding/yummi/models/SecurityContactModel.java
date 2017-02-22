package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 03/06/16.
 */
public class SecurityContactModel {

    @SerializedName("_phone")
    SecurityContactNumberModel phone;



    public SecurityContactModel(final String securityNumber){
        if (phone == null)
            phone = new SecurityContactNumberModel(securityNumber);

    }
}
