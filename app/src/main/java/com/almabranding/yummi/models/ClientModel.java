package com.almabranding.yummi.models;

import com.almabranding.yummi.models.third.GenderModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 19/04/16.
 */
public class ClientModel {

    @SerializedName("email")
    String email;

    @SerializedName("username")
    String username;

    @SerializedName("stageName")
    String stageName;

    @SerializedName("securityContact")
    SecurityContactModelResponse securityContact;

    public String getSecurityNumber() {
        if (securityContact != null)
            return securityContact.getSecNumber();

        return "";
    }
    public void setSecurityNumber(String id) {

        if (securityContact == null)
            this.securityContact = new SecurityContactModelResponse();

        if (this.securityContact.phone == null){
            this.securityContact.phone = new SecurityContactNumberModel(id);
        }

        this.securityContact.phone.number = id;


    }

    public String getStageName() {
        return stageName;
    }

    @SerializedName("cards")
    List<CardModel> cards;

    public List<CardModel> getCards() {
        return cards;
    }

    @SerializedName("id")
    String id;


    @SerializedName("signUpStep")
    int signUpStep = -1;

    public int getSignUpStep() {
        return signUpStep;
    }

    @SerializedName("genderId")
    String genderId;

    public String getGenderId() {
        return genderId;
    }


    public String getId() {
        return id;
    }


}
