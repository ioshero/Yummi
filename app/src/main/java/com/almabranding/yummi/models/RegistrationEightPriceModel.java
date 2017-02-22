package com.almabranding.yummi.models;

import com.almabranding.yummi.models.services.ServicePushModel;
import com.almabranding.yummi.models.services.ServicePushPriceModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegistrationEightPriceModel {

    @SerializedName("prices")
    List<ServicePushPriceModel> services;



    @SerializedName("imagePrice")
    int imagePrice;

    @SerializedName("chatPrice")
    int chatPrice;





    public RegistrationEightPriceModel(List<ServicePushPriceModel> services) {
        this.services = services;

    }


    public RegistrationEightPriceModel(List<ServicePushPriceModel> services, int imagePrice, int chatPrice ) {
        this.services = services;

        this.imagePrice = imagePrice;
        this.chatPrice = chatPrice;
    }


    public  List<ServicePushPriceModel> getServiceList(){
        return services;
    }
}
