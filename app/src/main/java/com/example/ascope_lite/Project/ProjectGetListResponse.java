package com.example.ascope_lite.Project;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProjectGetListResponse implements Serializable {
    //update coordinate
    @SerializedName("coordinateid")
    private int coordinateid;

    @SerializedName("crackid")
    private int crackid;

    //response
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("under")
    private int under;

    @SerializedName("upper")
    private int upper;

    @SerializedName("etc")
    private String etc;

    @SerializedName("report_filename")
    private String report_filename;

    @SerializedName("userid")
    private String userid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUnder() {
        return under;
    }

    public void setUnder(int under) {
        this.under = under;
    }

    public int getUpper() {
        return upper;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public String getReport_filename() {
        return report_filename;
    }

    public void setReport_filename(String report_filename) {
        this.report_filename = report_filename;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getCoordinateid() {
        return coordinateid;
    }

    public void setCoordinateid(int coordinateid) {
        this.coordinateid = coordinateid;
    }

    public int getCrackid() {
        return crackid;
    }

    public void setCrackid(int crackid) {
        this.crackid = crackid;
    }
}
