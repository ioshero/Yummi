package com.almabranding.yummi.models.second;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegSecondClient {

    @SerializedName("email")
    String email;

    @SerializedName("userType")
    String userType;

    @SerializedName("signUpStep")
    int signUpStep;

    @SerializedName("stageName")
    String stageName;

    public RegSecondClient(String email, String userType, int signUpStep, String stageName ) {
        this.email = email;
        this.userType = userType;
        this.signUpStep = signUpStep;
        this.stageName = stageName;
    }

    public String getEmail(){
        return email;
    }

    public String getStageName(){
        return stageName;
    }
}
