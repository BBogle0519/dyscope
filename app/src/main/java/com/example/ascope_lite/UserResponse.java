package com.example.ascope_lite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResponse implements Serializable {
    @SerializedName("last_login")
    String last_login;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("created_at")
    String created_at;

    @SerializedName("is_active")
    String is_active;

    @SerializedName("vip")
    String vip;

    @SerializedName("membership_start")
    String membership_start;

    @SerializedName("membership_end")
    String membership_end;

    @SerializedName("is_admin")
    String is_admin;

    @SerializedName("attempt")
    String attempt;

    @SerializedName("id")
    String id;

    @SerializedName("company_name")
    String company_name;

    @SerializedName("company_number")
    String company_number;

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getMembership_start() {
        return membership_start;
    }

    public void setMembership_start(String membership_start) {
        this.membership_start = membership_start;
    }

    public String getMembership_end() {
        return membership_end;
    }

    public void setMembership_end(String membership_end) {
        this.membership_end = membership_end;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_number() {
        return company_number;
    }

    public void setCompany_number(String company_number) {
        this.company_number = company_number;
    }
}