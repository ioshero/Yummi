package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 10/05/16.
 */
public class PaymentTokenModel {


    @SerializedName("token")
    String token;

    public PaymentTokenModel(String token ) {

        this.token = token;
    }
}
