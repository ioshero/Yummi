package com.almabranding.yummi.models.chat;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ioshero on 08/06/16.
 */
public class ChatModel implements Comparable<ChatModel>{

    /*
    {
    "status": 2,
    "createdAt": "2016-06-08T05:41:35.079Z",
    "id": "5757b00faaf42d0c15b23847",
    "clientId": "574c027bf7b28bc50bdffb62",
    "performerId": "5755382992c438747d370dc0",
    "performerName": "Desire",
    "cover": "https://s3-us-west-2.amazonaws.com/yummi-app/performers/5755382992c438747d370dc0/images/file_cropped_image1_1.jpg",
    "clientName": "AdamSKW"
  }
     */


    @SerializedName("clientName")
    String clientName;

    @SerializedName("cover")
    String cover;

    @SerializedName("performerName")
    String performerName;

    @SerializedName("performerId")
    String performerId;

    @SerializedName("clientId")
    String clientId;

    @SerializedName("id")
    String id;

    @SerializedName("createdAt")
    String createdAt;


    public Date getStartAtDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(createdAt.replace("T"," ").substring(0,18));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return convertedDate;
        }

        return convertedDate;
//        return startAt;
    }

    @SerializedName("status")
    int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCover() {
        return cover;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getPerformerId() {
        return performerId;
    }

    public String getPerformerName() {
        return performerName;
    }

    int unreaded = 0;

    public void setUnreaded(int unreaded) {
        this.unreaded = unreaded;
    }

    public int getUnreaded() {
        return unreaded;
    }


    @Override
    public int compareTo(ChatModel chatModel) {
        return this.getStartAtDate().compareTo(chatModel.getStartAtDate());
    }
}
