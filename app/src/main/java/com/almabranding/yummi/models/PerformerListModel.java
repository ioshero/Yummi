package com.almabranding.yummi.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioshero on 11/05/16.
 */
public class PerformerListModel {


    @SerializedName("stageName")
    String name;

    @SerializedName("location")
    ArrayList<Double> location;

    public ArrayList<Double> getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        if (location.size() > 1)
            return new LatLng(location.get(1), location.get(0));

        else return new LatLng(0,0);
    }

    public ImageModel getCover() {
        return cover;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SerializedName("id")
    String id;

    @SerializedName("cover")
    ImageModel cover;


    @SerializedName("distance")
    double distance = -1.0;

    public double getDistance() {
        return distance;
    }
}
