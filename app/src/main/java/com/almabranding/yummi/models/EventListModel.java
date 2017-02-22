package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ioshero on 28/04/16.
 */
public class EventListModel  {

    @SerializedName("numPerformers")
    int numPerformers;

    @SerializedName("status")
    int status;


    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public int getNumPerformers() {
        return numPerformers;
    }

    @SerializedName("id")
    String id;
//2016-07-27T11:18:24.000Z
    @SerializedName("startAt")
    String startAt;

    public String getStartAt() {
        return startAt;
    }



    public String getstartAtDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat stringFormat = new SimpleDateFormat("dd MMM. HH:mm", Locale.ENGLISH);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(startAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return stringFormat.format(convertedDate);
        }

        return stringFormat.format(convertedDate);
//        return startAt;
    }


    public String getId() {
        return id;
    }

    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    @SerializedName("numClients")
    int numClients;

    public int getNumClients() {
        return numClients;
    }

    @SerializedName("numServices")
    int numServices;

    public int getNumServices() {
        return numServices;
    }

    @SerializedName("cover")
    CoverModel cover;

    public CoverModel getCover() {
        return cover;
    }
}
