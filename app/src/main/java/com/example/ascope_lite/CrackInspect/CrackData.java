package com.example.ascope_lite.CrackInspect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CrackData implements Serializable {
    //update coordinate
    @SerializedName("coordinateid")
    private int coordinateid;

    @SerializedName("crackid")
    private int crackid;


    public int menu;
    public int sub_type;

    public String id;
    public String seq_update;

    @SerializedName("projectid")
    public String projectid;

    @SerializedName("planid")
    public String planid;

    @SerializedName("x")
    public String x;

    @SerializedName("y")
    public String y;

    //내부 외부
    @SerializedName("zone")
    public String zone;

    //천장, 벽면 등 위치
    @SerializedName("position")
    public String position;

    //폭
    @SerializedName("size")
    public String size;

    //길이
    @SerializedName("length")
    public String length;

    //박리, 박락, 기타 등
    @SerializedName("type")
    public String type;

    @SerializedName("img")
    public String img;

    @SerializedName("etc")
    public String etc;

    @SerializedName("auto_analyze")
    public String auto_analyze;

    public int getMenu() {
        return menu;
    }

    public void setMenu(int menu) {
        this.menu = menu;
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

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getPlanid() {
        return planid;
    }

    public void setPlanid(String planid) {
        this.planid = planid;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
