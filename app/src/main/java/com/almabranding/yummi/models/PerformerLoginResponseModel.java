package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class PerformerLoginResponseModel extends LoginResponseModel {


    @SerializedName("performer")
    PerformerModel performer;


    public PerformerModel getPerformer() {
        return performer;
    }
}
