package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 10/05/16.
 */
public class PaymentModel {

    @SerializedName("amount")
    int amount = 1;

    @SerializedName("description")
    String description;

    @SerializedName("token")
    String token;

    public PaymentModel(String token, String description ) {

        this.token = token;
        this.description = description;
    }
}
