package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 03/06/16.
 */
public class SecurityContactModelResponse {


    /*
    securityContact = {
            id = 57ac4ccd2f8192a7666f0e36;
            ownerId = 57694be1b86c3cec037fe726;
            ownerType = client;
            securityNumber = 1231888;
        };
    */
    @SerializedName("_phone")
    SecurityContactNumberModel phone;

    public String getSecNumber(){
        if (phone == null){
            phone = new SecurityContactNumberModel("");
        }
        return phone.number;
    }

    @SerializedName("id")
    String id;

    @SerializedName("ownerId")
    String ownerId;

    @SerializedName("ownerType")
    String ownerType;




}
