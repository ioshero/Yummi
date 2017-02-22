package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class UpdatePassModel {
    @SerializedName("oldPassword")
    String oldPassword;


    @SerializedName("newPassword")
    String newPassword;

    public UpdatePassModel(String oldPassword, String newPassword ) {
        if (oldPassword == null){
            oldPassword = "";
        }
        this.oldPassword = oldPassword;

        if (newPassword == null){
            newPassword = "";
        }
        this.newPassword = newPassword;

    }
}
