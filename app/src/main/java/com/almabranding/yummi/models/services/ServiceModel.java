package com.almabranding.yummi.models.services;

import com.almabranding.yummi.models.third.ServiceTypeModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class ServiceModel {
    @SerializedName("name")
    String name;

    @SerializedName("id")
    String id;

    @SerializedName("serviceTypeId")
    String serviceTypeId;

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    @SerializedName("duration")
    String duration;

    @SerializedName("serviceType")
    ServiceTypeModel serviceType;

    public ServiceTypeModel getServiceType() {
        return serviceType;
    }

    public ServiceModel(String name, String id , String duration) {
        if (name == null){
            name = "";
        }
        if (id == null){
            id = "";
        }
        this.duration = duration;

        this.name = name;
        this.id = id;
    }

    public ServiceModel(String name, String id ) {
        if (name == null){
            name = "";
        }
        if (id == null){
            id = "";
        }

        this.name = name;
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
