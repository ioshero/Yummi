package com.almabranding.yummi.models;

import com.almabranding.yummi.models.services.ServiceModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 21/04/16.
 */
public class ServiceRespondModel implements Comparable<ServiceRespondModel> {

    /*
    {
          "duration": 1,
          "accepted": false,
          "id": "575e8561530593086d8d799c",
          "serviceId": "56d71d434cfa5f4e31a5c4e7",
          "eventPerformerId": "575e8561530593086d8d7995",
          "service": {
            "name": "Sex",
            "duration": 1,
            "id": "56d71d434cfa5f4e31a5c4e7",
            "serviceTypeId": "56d7179f4cfa5f4e31a5c4d4"
          }
        }
     */


    @SerializedName("duration")
    double duration;



    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @SerializedName("accepted")
    boolean accepted;


    int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @SerializedName("eventPerformerId")
    String performerId;

    public String getPerformerId() {
        if (performerId == null)
            return "";
        return performerId;
    }

    public void setPerformerId(String performerId) {
        this.performerId = performerId;
    }

    @SerializedName("id")
    String id;

    @SerializedName("serviceId")
    String serviceId;

    public String getServiceId() {
        return serviceId;
    }



    @SerializedName("service")
    ServiceModel service;



    public ServiceRespondModel( ServiceModel service) {
        this.service = service;
    }

    public ServiceRespondModel( ServiceModel service, double dur, String performerId) {
        this.service = service;

        setPerformerId(performerId);
        this.duration = dur;
    }

    public ServiceModel getService() {
        return service;
    }


    @Override
    public int compareTo(ServiceRespondModel serviceRespondModel) {
        return this.service.getId().compareTo(serviceRespondModel.service.getId());
    }

    @Override
    public boolean equals(Object o) {
        try {
            return (this.service.getId()).equals(((ServiceRespondModel)o).service.getId());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}