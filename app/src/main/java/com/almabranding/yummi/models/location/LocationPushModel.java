package com.almabranding.yummi.models.location;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 10/05/16.
 */
public class LocationPushModel {

    @SerializedName("latitude")
    double latitude;

    @SerializedName("longitude")
    double longitude;

    @SerializedName("id")
    String id;

    public LocationPushModel(final double lat, final double lng, String id){
        this.latitude = lat;
        this.id = id;
        this.longitude = lng;
    }

    public void setLat(double lat) {
        this.latitude = lat;
    }

    public void setLng(double lng) {
        this.longitude = lng;
    }

    public double getLat() {
        return latitude;
    }

    public double getLng() {
        return longitude;
    }
}
