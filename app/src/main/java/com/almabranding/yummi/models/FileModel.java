package com.almabranding.yummi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ioshero on 19/04/16.
 */
public class FileModel {


    @SerializedName("name")
    String name;

    @SerializedName("path")
    String path;

    @SerializedName("id")
    String id;

    @SerializedName("src")
    String src;

    public String getSrc() {
        return src;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }


    public FileModel(String id, String name, String src, String path) {
        if (id == null) {
            id = "";
        }
        if (name == null) {
            name = "";
        }

        if (src == null) {
            src = "";
        }
        if (path == null) {
            path = "";
        }
        this.path = path;
        this.src = src;
        this.name = name;
        this.id = id;

    }


}
