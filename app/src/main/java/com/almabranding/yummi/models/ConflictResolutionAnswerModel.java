package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 27/07/16.
 */
public class ConflictResolutionAnswerModel {


    /*
      {
        "title": "Client is not here",
        "createdAt": "2016-07-25T14:03:11.978Z",
        "id": "57961c1f80e59df05aa0a2db",
        "categoryId": "57961c1f80e59df05aa0a2b2"
      }
     */

    @SerializedName("title")
    String title;

    @SerializedName("description")
    String description;

    public String getDescription() {
        return description;
    }

    @SerializedName("createdAt")
    String createdAt;

    @SerializedName("id")
    String id;

    @SerializedName("categoryId")
    String categoryId;

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

}
