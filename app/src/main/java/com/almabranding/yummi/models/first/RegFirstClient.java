package com.almabranding.yummi.models.first;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegFirstClient {

    @SerializedName("email")
    String email;

    @SerializedName("userType")
    String userType;

    @SerializedName("signUpStep")
    int signUpStep;

    @SerializedName("id")
    String id;

    @SerializedName("token")
    RegistrationToken token;

    public RegFirstClient(String email, String id, String userType,int signUpStep, RegistrationToken token ) {
        this.email = email;
        this.userType = userType;
        this.signUpStep = signUpStep;
        this.id = id;

        this.token = token;
    }

    public String getEmail(){
        return email;
    }
    public String getUserId(){
        return id;
    }
    public RegistrationToken getUserToken(){
        return token;
    }
}
