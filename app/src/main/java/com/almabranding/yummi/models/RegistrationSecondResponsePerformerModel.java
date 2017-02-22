package com.almabranding.yummi.models;

import com.almabranding.yummi.models.second.RegSecondClient;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationSecondResponsePerformerModel {

    @SerializedName("performer")
    RegSecondClient performer;

    public RegistrationSecondResponsePerformerModel(RegSecondClient performer ) {
        this.performer = performer;
    }

    public RegSecondClient getClient(){
        return performer;
    }
}
