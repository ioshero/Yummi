package com.almabranding.yummi.models;

import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.models.services.ServicePushModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationEightModel {

    @SerializedName("services")
    List<ServicePushModel> services;



    public RegistrationEightModel(List<ServicePushModel> services ) {
        this.services = services;

    }


    public  List<ServicePushModel> getServiceList(){
        return services;
    }
}
