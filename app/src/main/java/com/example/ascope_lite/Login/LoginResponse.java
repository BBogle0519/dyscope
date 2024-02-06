package com.example.ascope_lite.Login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    //조사자
    @SerializedName("projectid")
    private String projectid;

    //관리자
    @SerializedName("success")
    private String success;

    //공통
    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("attempt")
    private String attempt;

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
