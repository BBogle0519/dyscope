package com.example.ascope_lite.CamTest_v01;

public class clsIcon {
    public int iIndex = -1;
    public String sIndex = "";
    public float iX = 0;
    public float iY = 0;
    public float iScale = 1.0f;

    public clsIcon(String isIndex, String isX, String isY, String isScale){
        sIndex=isIndex;
        iIndex=Integer.valueOf(sIndex);
        iX=Float.valueOf(isX);
        iY=Float.valueOf(isY);
        iScale=Float.valueOf(isScale);
    }
}
