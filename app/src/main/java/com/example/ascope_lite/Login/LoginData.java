package com.example.ascope_lite.Login;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("userid")
    public String userid;

    @SerializedName("password")
    public String password;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
