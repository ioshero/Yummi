package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ioshero on 03/05/16.
 */
public class NotificationModel {


    @SerializedName("type")
    String type;

    public String getType() {
        return type;
    }

    @SerializedName("eventId")
    String eventId;



    @SerializedName("createdAt")
    String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }


    //5 jul 12 : 37

    public String getCreatedAtString() {


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(createdAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return createdAt.replace("T"," ").substring(0,18);
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM. HH:mm", Locale.ENGLISH);
        dateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));


        return dateFormat2.format(convertedDate.getTime());
    }


    @SerializedName("clientName")
    String clientName;

    public String getClientName() {
        return clientName;
    }

    public String getEventId() {
        return eventId;
    }

    @SerializedName("serviceName")
    String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    @SerializedName("eventName")
    String eventName;

    public String getEventName() {
        return eventName;
    }

    @SerializedName("id")
    String id;

    @SerializedName("requestId")
    String requestId;

    @SerializedName("chatId")
    String chatId;

    public String getChatId() {
        return chatId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getId() {
        return id;
    }
}
