package com.almabranding.yummi.models;

import com.almabranding.yummi.models.third.GenderModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 19/04/16.
 */
public class PerformerModel {

    @SerializedName("stageName")
    String name;

    @SerializedName("birthDate")
    String birthDate;

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

    public String getBirthDate() {
        return birthDate;
    }

    @SerializedName("height")
    double height;

    int tip = 10;

    public String getTipStr() {
        return "$ " + String.valueOf(tip);
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    @SerializedName("myFavourite")
    boolean myFavourite = false;

    public boolean isMyFavourite() {
        return myFavourite;
    }

    public double getHeight() {
        return height;
    }

    @SerializedName("id")
    String id;

    int tipPrice = 0;

    public void setTipPrice(int tipPrice) {
        this.tipPrice = tipPrice;
    }

    public int getTipPrice() {
        return tipPrice;
    }

    @SerializedName("chatPrice")
    int chatPrice = 0;

    @SerializedName("imagePrice")
    int imagePrice = 0;

    public int getChatPrice() {
        return chatPrice;
    }

    public int getImagePrice() {
        return imagePrice;
    }

    @SerializedName("signUpStep")
    int signUpStep = -1;

    public int getSignUpStep() {
        return signUpStep;
    }

    @SerializedName("gender")
    GenderModel gender;

    public GenderModel getGender() {
        return gender;
    }

    @SerializedName("bustSize")
    GenderModel bustSize;

    @SerializedName("genderId")
    String genderId;

    public String getGenderId() {
        return genderId;
    }

    public GenderModel getBustSize() {
        return bustSize;
    }


    @SerializedName("bodyType")
    GenderModel bodyType;

    public GenderModel getbodyType() {
        return bodyType;
    }

    @SerializedName("hairColor")
    GenderModel hairColor;

    public GenderModel gethairColor() {
        return hairColor;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PerformerModel(String name, String id, List<ImageModel> images) {
        if (id == null) {
            id = "";
        }
        if (name == null) {
            name = "";
        }

        this.id = id;
        this.name = name;
        this.images = images;

    }

    public PerformerModel(String name) {
        id = "";
        this.name = name;


    }

    @SerializedName("prices")
    List<PriceModel> prices;

    public List<PriceModel> getPrices() {
        return prices;
    }

    @SerializedName("services")
    List<Service> services;

    public List<Service> getServices() {
        return services;
    }

    @SerializedName("images")
    List<ImageModel> images;

    public List<ImageModel> getImages() {
        return images;
    }

    public String getFirstImagePath() {
        if (images.size() > 0) {
            return images.get(0).getFirstImagePath();
        }
        return "";
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof PerformerModel)
            return getId().equals(((PerformerModel)o).getId());

        if (o instanceof EventPerformerModel)
            return getId().equals(((EventPerformerModel)o).getPerformerId());

        return super.equals(o);

    }


}
