package com.example.ascope_lite.CrackInspect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CrackAutoResponse implements Serializable {
    @SerializedName("crack_size")
    public float crack_size;

    @SerializedName("crack_length")
    public float crack_length;

    @SerializedName("crack_box_size")
    public float crack_box_size;

    @SerializedName("filename")
    public String filename;


    public float getCrack_length() {
        return crack_length;
    }

    public void setCrack_length(float crack_length) {
        this.crack_length = crack_length;
    }

    public float getCrack_box_size() {
        return crack_box_size;
    }

    public void setCrack_box_size(float crack_box_size) {
        this.crack_box_size = crack_box_size;
    }

    public float getCrack_size() {
        return crack_size;
    }

    public void setCrack_size(float crack_size) {
        this.crack_size = crack_size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
