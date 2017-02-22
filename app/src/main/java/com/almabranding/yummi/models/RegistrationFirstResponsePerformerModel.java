package com.almabranding.yummi.models;

import com.almabranding.yummi.models.first.RegFirstClient;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationFirstResponsePerformerModel {


    @SerializedName("performer")
    RegFirstClient performer;



    public RegistrationFirstResponsePerformerModel(RegFirstClient client ) {
        this.performer = client;
    }

    public RegFirstClient getClient(){
        return performer;
    }
}
