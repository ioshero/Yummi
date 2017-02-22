package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 03/06/16.
 */
public class BankModel {

    /*
    {"bankNumber": "string",
  "bankOfficeNumber": "string",
  "accountNumber": "string",
  "ownerIdNumber": "string",
  "ownerName": "string",
  "id": "string",
  "ownerId": "string",
  "ownerType": "string"}
     */

    @SerializedName("bankNumber")
    String bankNumber;

    @SerializedName("accountNumber")
    String accountNumber;

    @SerializedName("ownerIdNumber")
    String ownerIdNumber;

    @SerializedName("bankOfficeNumber")
    String bankOfficeNumber;

    @SerializedName("ownerName")
    String ownerName;

    @SerializedName("id")
    String id;

    @SerializedName("ownerId")
    String ownerId;
    @SerializedName("ownerType")
    String ownerType;


    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public String getBankOfficeNumber() {
        return bankOfficeNumber;
    }

    public String getOwnerIdNumber() {
        return ownerIdNumber;
    }

    public String getBSB()
    {
        return bankNumber+"-"+bankOfficeNumber;
    }
    public BankModel(String bankNumber, String accountNumber, String ownerIdNumber, String bankOfficeNumber){
        this.bankNumber = bankNumber;
        this.accountNumber = accountNumber;
        this.ownerIdNumber = ownerIdNumber;
        this.bankOfficeNumber = bankOfficeNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public void setBankOfficeNumber(String bankOfficeNumber) {
        this.bankOfficeNumber = bankOfficeNumber;
    }

    public void setOwnerIdNumber(String ownerIdNumber) {
        this.ownerIdNumber = ownerIdNumber;
    }

}
