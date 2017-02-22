package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 29/04/16.
 */
public class CoverModel {


    @SerializedName("file")
    FileModel file;


    public FileModel getFile() {
        return file;
    }
}
