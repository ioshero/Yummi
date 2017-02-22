package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class ResetPassModel {
    @SerializedName("email")
    String email;


    public ResetPassModel(String email ) {
        if (email == null){
            email = "";
        }
        this.email = email;
    }
}
