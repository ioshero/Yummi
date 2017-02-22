package com.almabranding.yummi.models;

import android.widget.ImageButton;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ioshero on 19/04/16.
 */
public class ImageModel {


    public ImageModel(FileModel files, String id) {
        this.file = files;
        if (id == null) {
            id = "";
        }
        this.id = id;
    }

    @SerializedName("id")
    String id;

    public String getId() {
        return id;
    }

    @SerializedName("file")
    FileModel file;

    public FileModel getImages() {
        return file;
    }

    public String getFirstImagePath() {
        if (file != null)
                return file.getSrc();

        return "";
    }



}
