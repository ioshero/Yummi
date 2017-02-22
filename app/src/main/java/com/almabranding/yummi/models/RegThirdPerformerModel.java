package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegThirdPerformerModel {

    @SerializedName("id")
    String id;

    @SerializedName("genderId")
    String genderId;


    public RegThirdPerformerModel(String id, String genderId ) {
        if (id == null)
            id = "";
        if (genderId == null)
            genderId = "";

        this.id = id;
        this.genderId = genderId;
    }


}
