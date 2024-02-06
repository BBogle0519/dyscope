package com.example.ascope_lite.CrackInspect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CrackListResponse implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("coordinate")
    private CrackCoordinateData coordinate;

    @SerializedName("zone")
    private String zone;

    //천장, 벽면 등 위치
    @SerializedName("position")
    private String position;

    //박리, 박락, 기타 등
    @SerializedName("type")
    private String type;

    //폭
    @SerializedName("size")
    private String size;

    //길이
    @SerializedName("length")
    private String length;

    @SerializedName("img")
    private String img;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("etc")
    private String etc;

    @SerializedName("auto_analyze")
    private String auto_analyze;

    @SerializedName("project")
    private String project;

    @SerializedName("userid")
    private String userid;

    @SerializedName("plan")
    private String plan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CrackCoordinateData getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(CrackCoordinateData coordinate) {
        this.coordinate = coordinate;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public String getAuto_analyze() {
        return auto_analyze;
    }

    public void setAuto_analyze(String auto_analyze) {
        this.auto_analyze = auto_analyze;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}
