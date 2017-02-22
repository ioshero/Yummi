package com.almabranding.yummi.models.third;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 04/05/16.
 */
public class GuestClientModel {

    /*
    "client": {
        "stageName": "adam89",
        "userType": 0,
        "banStatus": 2,
        "username": "adam89@rap.sk",
        "email": "adam89@rap.sk",
        "id": "5728c60cac91acbe0ce4b4e4",
        "genderId": "56d8533b083e3e9e40254037",
        "signUpStep": 2,
        "gender": {
          "name": "Fluid",
          "id": "56d8533b083e3e9e40254037"
        },
     */

    @SerializedName("id")
    String id;

    @SerializedName("stageName")
    String stageName;

    public String getStageName() {
        return stageName;
    }

    public String getId() {
        return id;
    }


    public GuestClientModel(String stageName){
        this.stageName = stageName;
    }
}
