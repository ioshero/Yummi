package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 17/05/16.
 */
public class PriceModel {
    /*
    {
      "amount": 100,
      "createdAt": "2016-05-15T08:17:51.285Z",
      "updatedAt": "2016-05-15T08:17:51.285Z",
      "id": "573830af33e46f073aa4b139",
      "serviceTypeId": "56d717584cfa5f4e31a5c4d2",
      "performerId": "572dd4f4fcd7fcd338aa94d7",
      "serviceTypeName": "Topless services"
    }
     */

    @SerializedName("amount")
    int amount;

    @SerializedName("serviceTypeId")
    String serviceTypeId;

    @SerializedName("id")
    String id;

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }


}
