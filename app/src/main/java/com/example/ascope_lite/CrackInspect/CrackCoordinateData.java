package com.example.ascope_lite.CrackInspect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CrackCoordinateData implements Serializable {
    @SerializedName("id")
    int id;

    @SerializedName("x")
    int x;

    @SerializedName("y")
    int y;

    @SerializedName("index")
    int index;

    @SerializedName("project")
    int project;

    @SerializedName("plan")
    int plan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getPlan() {
        return plan;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }
}
