package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class UpdateMailModel {
    @SerializedName("email")
    String email;


    @SerializedName("password")
    String password;

    public UpdateMailModel(String email, String password ) {
        if (email == null){
            email = "";
        }
        this.email = email;

        if (password == null){
            password = "";
        }
        this.password = password;

    }
}
