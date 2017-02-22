package com.almabranding.yummi.models.services;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class ServicePushModel {
    @SerializedName("serviceId")
    String serviceId;

    @SerializedName("availability")
    int availability;

    public ServicePushModel(int availability, String serviceId ) {

        if (serviceId == null){
            serviceId = "";
        }
        this.availability = availability;
        this.serviceId = serviceId;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getAvailability() {
        return availability;
    }

    public String getServiceId() {
        return serviceId;
    }
}
