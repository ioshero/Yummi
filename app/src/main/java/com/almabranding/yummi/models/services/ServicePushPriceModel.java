package com.almabranding.yummi.models.services;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class ServicePushPriceModel {
    @SerializedName("serviceTypeId")
    String serviceTypeId;

    @SerializedName("amount")
    int price;

    public ServicePushPriceModel(int availability, String serviceId ) {

        if (serviceId == null){
            serviceId = "";
        }
        this.price = availability;
        this.serviceTypeId = serviceId;
    }

    public void setPrice(int availability) {
        this.price = availability;
    }

    public void setServiceId(String serviceId) {
        this.serviceTypeId = serviceId;
    }

    public int getPrice() {
        return price;
    }

    public String getServiceId() {
        return serviceTypeId;
    }
}
