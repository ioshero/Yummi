package com.almabranding.yummi.models.third;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 02/05/16.
 */
public class ServiceTypeModel {
    @SerializedName("name")
    String name;

    @SerializedName("id")
    String id;

    public ServiceTypeModel(String name, String id ) {
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
