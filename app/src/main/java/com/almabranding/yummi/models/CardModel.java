package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 19/04/16.
 */
public class CardModel {



    @SerializedName("token")
    String token;

    @SerializedName("digits")
    String digits;

    @SerializedName("default")
    boolean def;

    public boolean isDefault() {
        return def;
    }

    @SerializedName("id")
    String id;


    public String getId() {
        return id;
    }

    @SerializedName("type")
    String type;

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDigits() {
        return digits;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    @SerializedName("expirationMonth")
    int expirationMonth;


    @SerializedName("expirationYear")
    int expirationYear;


    @SerializedName("clientId")
    String clientId;











}
