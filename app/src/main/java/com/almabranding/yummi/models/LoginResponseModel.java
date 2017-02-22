package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class LoginResponseModel {
    @SerializedName("id")
    String id;

    @SerializedName("ttl")
    long ttl;

    @SerializedName("userId")
    String userId;

    @SerializedName("client")
    ClientModel client;

    public ClientModel getClient() {
        return client;
    }

    public LoginResponseModel(){

    }

    public LoginResponseModel(String id, String ttl, String userId ) {
        this.id = id;
        this.id = ttl;
        this.userId = userId;
    }

    public String getUserId(){
        return userId;
    }

    public String getId() {
        return id;
    }
}
