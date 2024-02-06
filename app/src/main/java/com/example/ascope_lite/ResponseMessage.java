package com.example.ascope_lite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseMessage implements Serializable {
    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
