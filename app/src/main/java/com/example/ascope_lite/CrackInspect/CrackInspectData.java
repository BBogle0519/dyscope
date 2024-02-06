package com.example.ascope_lite.CrackInspect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CrackInspectData implements Serializable {
    public int menu;
    public int sub_type;

    public String id;
    public String seq_update;

    @SerializedName("seq")
    public int seq;

    @SerializedName("facilityid")
    public String facilityid;

    //내부 외부
    @SerializedName("zone")
    public String zone;

    //천장, 벽면 등 위치
    @SerializedName("position")
    public String position;

    @SerializedName("type")
    public String type;

    //폭
    @SerializedName("crack_size")
    public String crack_size;

    //길이
    @SerializedName("crack_length")
    public String crack_length;

    //면적
    @SerializedName("crack_box_size")
    public String crack_box_size;

    @SerializedName("img")
    public String img;

    @SerializedName("floor")
    public String floor;

    @SerializedName("report")
    public String report;

    @SerializedName("etc")
    public String etc;

    @SerializedName("plan_coordinate")
    public String plan_coordinate;

    @SerializedName("auto_analyze")
    public String auto_analyze;

    public String getAuto_analyze() {
        return auto_analyze;
    }

    public void setAuto_analyze(String auto_analyze) {
        this.auto_analyze = auto_analyze;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getCrack_length() {
        return crack_length;
    }

    public void setCrack_length(String crack_length) {
        this.crack_length = crack_length;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPlan_coordinate() {
        return plan_coordinate;
    }

    public void setPlan_coordinate(String plan_coordinate) {
        this.plan_coordinate = plan_coordinate;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public int getMenu() {
        return menu;
    }

    public void setMenu(int menu) {
        this.menu = menu;
    }

    public String getFacilityid() {
        return facilityid;
    }

    public void setFacilityid(String facilityid) {
        this.facilityid = facilityid;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCrack_size() {
        return crack_size;
    }

    public void setCrack_size(String crack_size) {
        this.crack_size = crack_size;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCrack_box_size() {
        return crack_box_size;
    }

    public void setCrack_box_size(String crack_box_size) {
        this.crack_box_size = crack_box_size;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public int getSub_type() {
        return sub_type;
    }

    public void setSub_type(int sub_type) {
        this.sub_type = sub_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeq_update() {
        return seq_update;
    }

    public void setSeq_update(String seq_update) {
        this.seq_update = seq_update;
    }
}
