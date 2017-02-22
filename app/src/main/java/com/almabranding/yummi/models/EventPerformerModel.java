package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 29/04/16.
 */
public class EventPerformerModel {

    @SerializedName("performerId")
    String performerId;


    @SerializedName("eventId")
    String eventId;

    @SerializedName("id")
    String id;

    public String getId() {
        return id;
    }

    @SerializedName("performer")
    PerformerModel performer;

    @SerializedName("services")
    List<ServiceRespondModel> services;

    public List<ServiceRespondModel> getServices() {
        return services;
    }


    public EventPerformerModel(String performerId, String eventId, PerformerModel per){
        this.performerId = performerId;
        this.eventId = eventId;
        this.performer = per;

    }

    public EventPerformerModel(String performerId, PerformerModel per){
        this.performerId = performerId;
        this.performer = per;

    }

    public void setPerformer(PerformerModel performer) {
        this.performer = performer;
    }

    public PerformerModel getPerformer() {
        return performer;
    }

    public String getPerformerId() {
        return performerId;
    }

    public String getEventId() {
        return eventId;
    }


}
