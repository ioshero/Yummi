package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 16/05/16.
 */
public class PerformerUpdateModel {


    @SerializedName("stageName")
    String stageName;

    @SerializedName("hairColorId")
    String hairColorId;
    @SerializedName("bodyTypeId")
    String bodyTypeId;
    @SerializedName("bustSizeId")
    String bustSizeId;

    @SerializedName("securityNumber")
    String securityNumber;


    public PerformerUpdateModel(String stageName, String hairColorId, String bodyTypeId , String bustSizeId,String securityNumber) {
        if (stageName == null){
            stageName = "";
        }
        if (bodyTypeId == null){
            bodyTypeId = "";
        }
        this.stageName = stageName;

        if (securityNumber == null){
            securityNumber = "";
        }
        this.securityNumber = securityNumber;

        this.bodyTypeId = bodyTypeId;

        if (bustSizeId == null){
            bustSizeId = "";
        }
        this.bustSizeId = bustSizeId;

        if (hairColorId == null){
            hairColorId = "";
        }
        this.hairColorId = hairColorId;

    }

}
