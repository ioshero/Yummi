package com.almabranding.yummi.models.first;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationToken {
    @SerializedName("id")
    String id;

    @SerializedName("ttl")
    long ttl;


    public RegistrationToken(String id, String ttl ) {
        this.id = id;
        this.id = ttl;
    }

    public String getUserId(){
        return id;
    }
}
