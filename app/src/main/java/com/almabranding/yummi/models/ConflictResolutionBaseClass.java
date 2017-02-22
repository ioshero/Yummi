package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioshero on 27/07/16.
 */
public class ConflictResolutionBaseClass {

    /*
    {
    "name": "Problem with the location",
    "userType": "performer",
    "createAt": "2016-07-25T14:03:11.931Z",
    "id": "57961c1f80e59df05aa0a2b1",
    "answers": [
      {
        "title": "Can't get in",
        "description": "If you are notifying the client, please note the system will only allow notifications to be sent at the following intervals. 5 minutes between the first and second notifications and one minute in between after that. After 10 minutes submit your claim below.",
        "createdAt": "2016-07-25T14:03:11.970Z",
        "id": "57961c1f80e59df05aa0a2d1",
        "categoryId": "57961c1f80e59df05aa0a2b1"
      }
     */

    @SerializedName("name")
    String name;

    @SerializedName("userType")
    String userType;

    @SerializedName("createAt")
    String createAt;

    @SerializedName("id")
    String id;

    @SerializedName("answers")
    List<ConflictResolutionAnswerModel> answers;

    public String getId() {
        return id;
    }

    public ArrayList<ConflictResolutionAnswerModel> getAnswers() {
        if (answers == null)
            return new ArrayList<ConflictResolutionAnswerModel>();

        return new ArrayList<ConflictResolutionAnswerModel>(answers);
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }


}
