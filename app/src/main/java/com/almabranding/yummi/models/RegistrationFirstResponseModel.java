package com.almabranding.yummi.models;

import com.almabranding.yummi.models.first.RegFirstClient;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationFirstResponseModel {


    @SerializedName("client")
    RegFirstClient client;



    public RegistrationFirstResponseModel(RegFirstClient client ) {
        this.client = client;
    }

    public RegFirstClient getClient(){
        return client;
    }
}
