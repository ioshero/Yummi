package com.almabranding.yummi.models.chat;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 08/06/16.
 */
public class ChatMessageModel {

    /*
    {
    "message": "test message",
    "createdAt": "2016-06-08T06:54:00.061Z",
    "read": false,
    "id": "5757c108aaf42d0c15b23859",
    "chatId": "5757b00faaf42d0c15b23847",
    "ownerId": "5755382992c438747d370dc0",
    "ownerType": "performer"
  }
     */



    @SerializedName("message")
    String message;

    @SerializedName("createdAt")
    String createdAt;

    @SerializedName("read")
    boolean read;

    @SerializedName("id")
    String id;

    @SerializedName("chatId")
    String chatId;

    @SerializedName("ownerId")
    String ownerId;

    @SerializedName("ownerType")
    String ownerType;

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public String getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }


}
