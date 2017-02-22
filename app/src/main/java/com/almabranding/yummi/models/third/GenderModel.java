package com.almabranding.yummi.models.third;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 12/04/16.
 */
public class GenderModel {
    @SerializedName("name")
    String name;

    @SerializedName("id")
    String id;

    public GenderModel(String name, String id ) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
