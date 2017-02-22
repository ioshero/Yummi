package com.almabranding.yummi.models;

import com.almabranding.yummi.models.second.RegSecondClient;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationSecondResponseModel {

    @SerializedName("client")
    RegSecondClient client;

    public RegistrationSecondResponseModel(RegSecondClient client ) {
        this.client = client;
    }

    public RegSecondClient getClient(){
        return client;
    }
}
