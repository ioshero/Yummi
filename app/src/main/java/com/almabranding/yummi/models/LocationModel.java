package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 28/04/16.
 */
public class LocationModel {

    @SerializedName("lat")
    double lat;

    @SerializedName("lng")
    double lng;


    public LocationModel(final double lat, final double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
