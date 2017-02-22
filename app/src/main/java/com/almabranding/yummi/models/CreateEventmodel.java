package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ioshero on 28/04/16.
 */
public class CreateEventmodel {


    private transient List<EventPerformerModel> offline_performers;

    public List<EventPerformerModel> getOffline_performers() {
        if (offline_performers == null)
            offline_performers = new ArrayList<EventPerformerModel>();

        return offline_performers;
    }

    public void setOffline_performers(List<EventPerformerModel> offline_performers) {
        this.offline_performers = offline_performers;
    }

    private transient List<ServiceRespondModel> offline_services;

    public List<ServiceRespondModel> getOffline_services() {
        if (offline_services == null)
            offline_services = new ArrayList<ServiceRespondModel>();

        return offline_services;
    }

    public void setOffline_services(List<ServiceRespondModel> offline_services) {
        this.offline_services = offline_services;
    }

    private ArrayList<String> offline_guests;

    public ArrayList<String> getOffline_guests() {
        if (offline_guests == null)
            offline_guests = new ArrayList<String>();
        return offline_guests;
    }

    public void setOffline_guests(ArrayList<String> offline_guests) {
        this.offline_guests = offline_guests;
    }

    @SerializedName("name")
    String name;

    @SerializedName("address")
    String address;

    public LocationModel getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    @SerializedName("ownerId")
    String ownerId;

    @SerializedName("startAt")
    String startAt;

    @SerializedName("location")
    LocationModel location;

    public String getName() {
        return name;
    }

    public String getStartAt() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(startAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return startAt;
        }

        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        return dateformat.format(convertedDate.getTime());
//        return startAt;
    }

    public String getStartAtString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat stringFormat = new SimpleDateFormat("HH:mm dd MMM. yyyy", Locale.ENGLISH);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(startAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return stringFormat.format(convertedDate);
        }

        return stringFormat.format(convertedDate);
    }

    public Date getStartAtDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(startAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return convertedDate;
        }

        return convertedDate;
//        return startAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }
}
