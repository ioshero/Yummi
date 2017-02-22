package com.almabranding.yummi.models;

import com.almabranding.yummi.fragments.PerformerListFragment;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ioshero on 28/04/16.
 */
public class EventModel extends CreateEventmodel {

    @SerializedName("paid")
    boolean paid;

    @SerializedName("id")
    String id;


    @SerializedName("endAt")
    String endAt;

    public String getEndAt() {
        return endAt;
    }

    public Date getEndAtDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(endAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return convertedDate;
        }

        return convertedDate;
//        return startAt;
    }

    @SerializedName("status")
    int status;

    @SerializedName("owner")
    ClientModel owner;

    public ClientModel getOwner() {
        return owner;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    @SerializedName("acceptedByClients")
    boolean acceptedByClients;

    @SerializedName("acceptedByPerformers")
    boolean acceptedByPerformers;

    @SerializedName("clients")
    List<GuestModel> clients;

    public List<GuestModel> getClients() {
        if (clients == null)
            clients = new ArrayList<GuestModel>();
        return clients;
    }

    @SerializedName("performers")
    List<EventPerformerModel> performers;

    @SerializedName("services")
    List<ServiceRespondModel> services;

    public List<ServiceRespondModel> getServices() {
        if (services == null)
            services = new ArrayList<ServiceRespondModel>();
        return services;
    }

    public List<EventPerformerModel> getPerformers() {
        if (performers == null)
            performers = new ArrayList<EventPerformerModel>();
        return performers;
    }

    public boolean isAcceptedByClients() {
        return acceptedByClients;
    }

    public boolean isAcceptedByPerformers() {
        return acceptedByPerformers;
    }

    public void setAcceptedByClients(boolean acceptedByClients) {
        this.acceptedByClients = acceptedByClients;
    }

    public void setAcceptedByPerformers(boolean acceptedByPerformers) {
        this.acceptedByPerformers = acceptedByPerformers;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
