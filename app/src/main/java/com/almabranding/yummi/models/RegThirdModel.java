package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 11/04/16.
 */
public class RegThirdModel {



    @SerializedName("genderId")
    String genderId;

    @SerializedName("interestedInId")
    String interestedInId;

    public RegThirdModel( String genderId, String interestedInId ) {

        if (genderId == null)
            genderId = "";
        if (interestedInId == null)
            interestedInId = "";

        this.genderId = genderId;
        this.interestedInId = interestedInId;
    }


}
