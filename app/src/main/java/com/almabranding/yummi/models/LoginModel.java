package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class LoginModel {
    @SerializedName("email")
    String email;

    @SerializedName("deviceType")
    String deviceType = "android";

    @SerializedName("deviceToken")
    String deviceToken;

    @SerializedName("password")
    String password;



    public LoginModel(String email, String password , String deviceToken) {
        if (email == null){
            email = "";
        }
        if (password == null){
            password = "";
        }
        if (deviceToken == null){
            deviceToken = "";
        }
        this.deviceToken = deviceToken;
        this.email = email;
        this.password = password;
    }
}
