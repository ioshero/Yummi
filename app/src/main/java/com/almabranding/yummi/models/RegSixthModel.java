package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegSixthModel {
    @SerializedName("id")
    String id;

    @SerializedName("order")
    int order;

    public RegSixthModel(String id, int order ) {
        if (id == null){
            id = "";
        }

        this.id = id;
        this.order = order;
    }
}
