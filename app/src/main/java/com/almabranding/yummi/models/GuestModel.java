package com.almabranding.yummi.models;

import com.almabranding.yummi.models.third.GuestClientModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 04/05/16.
 */
public class GuestModel {

    /*
    "clients": [
    {
      "status": 0,
      "id": "5729aaeeda1fca5b167de372",
      "eventId": "5721f9e641c69aaa49c6109a",
      "clientId": "5728c60cac91acbe0ce4b4e4",
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
        "interestedIn": [
          {
            "name": "Female",
            "id": "56d8533b083e3e9e40254036"
          }
        ]
      }
    }
    ]
     */

    @SerializedName("id")
    String id;

    public String getId() {
        return id;
    }

    @SerializedName("clientId")
    String clientId;

    public GuestModel(String id, String name){
        this.id = id;

        client = new GuestClientModel(name);

    }
    public GuestModel(){

    }
    @SerializedName("client")
    GuestClientModel client;

    public GuestClientModel getClient() {
        return client;
    }

}
