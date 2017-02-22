package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 03/06/16.
 */
public class MediaModel {


    /*
     "performerId": "57447746759e2d2e3ee33f3a",
    "performerName": "Natalie",
    "images": [
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/1464853545046_file_cropped_image1.jpg",
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/testImage57500b65658e410838351cdd.jpeg",
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/testImage57501e68658e410838351d04.jpeg",
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/testImage57503341658e410838351d10.jpeg",
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/testImage5750337f658e410838351d14.jpeg",
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/testImage5750338a658e410838351d17.jpeg",
      "https://s3-us-west-2.amazonaws.com/yummi-app/clients/574c027bf7b28bc50bdffb62/images/testImage57503615658e410838351d23.jpeg"
    ]

     */

    @SerializedName("performerId")
    String performerId;

    @SerializedName("performerName")
    String performerName;


    @SerializedName("images")
    List<String> images;


    public List<String> getImages() {
        return images;
    }

    public String getPerformerId() {
        return performerId;
    }

    public String getPerformerName() {
        return performerName;
    }

    @Override
    public boolean equals(Object o) {
        return performerId.equalsIgnoreCase(((MediaModel)o).getPerformerId());
    }
}
