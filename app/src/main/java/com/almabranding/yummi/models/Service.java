package com.almabranding.yummi.models;

import com.almabranding.yummi.models.services.ServiceModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 21/04/16.
 */
public class Service {
    @SerializedName("availability")
    int availability;

    private double duration;

    private String uid;

    private int price;

    public int getPrice() {
        if (price < 0)
            return 0;

        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public double getDuration() {
        return duration;
    }

    public String getStringDuration() {
        return String.valueOf(duration);
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }


    @SerializedName("service")
    ServiceModel service;

    public Service(int availability, ServiceModel service) {
        this.availability = availability;
        this.service = service;
    }

    public Service(int availability, ServiceModel service, int price) {
        this.price = price;
        this.availability = availability;
        this.service = service;
    }


    public int getAvailability() {
        return availability;
    }

    public ServiceModel getService() {
        return service;
    }


    @Override
    public boolean equals(Object o) {
        return service.getId().equalsIgnoreCase(((Service) o).getService().getId());
    }
}