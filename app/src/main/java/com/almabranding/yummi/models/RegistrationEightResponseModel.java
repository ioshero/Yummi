package com.almabranding.yummi.models;

import com.almabranding.yummi.models.second.RegSecondClient;
import com.almabranding.yummi.models.services.ServiceModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationEightResponseModel {

    @SerializedName("services")
    List<ServiceModel> services;

    @SerializedName("name")
    String name;

    @SerializedName("id")
    String id;

    public RegistrationEightResponseModel( List<ServiceModel> services, String name, String id ) {
        this.services = services;

        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public  List<ServiceModel> getServiceList(){
        return services;
    }
}
