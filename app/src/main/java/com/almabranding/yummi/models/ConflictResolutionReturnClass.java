package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioshero on 27/07/16.
 */
public class ConflictResolutionReturnClass {

    /*
 //    {
//        "answerId": "string”,
//        "description": "string"
//    }
     */

    @SerializedName("answerId")
    String answerId;

    @SerializedName("description")
    String description;

    public ConflictResolutionReturnClass(String answerId, String description) {
        if (answerId == null)
            answerId = "";
        if (description == null) {
            description = "";
        }

        this.answerId = answerId;
        this.description = description;
    }


}
