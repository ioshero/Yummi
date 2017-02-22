package com.almabranding.yummi.models.third;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegThirdClient {


    @SerializedName("id")
    String id;
    @SerializedName("genderId")
    String genderId;

    @SerializedName("email")
    String email;

    @SerializedName("userType")
    String userType;

    @SerializedName("signUpStep")
    int signUpStep;

    @SerializedName("stageName")
    String stageName;

    public RegThirdClient(String id, String genderId, String email, String userType, int signUpStep, String stageName) {

        this.id =id;
        this.genderId = genderId;
        this.email = email;
        this.userType = userType;
        this.signUpStep = signUpStep;
        this.stageName = stageName;
    }

    public String getEmail() {
        return email;
    }

    public String getStageName() {
        return stageName;
    }
}
