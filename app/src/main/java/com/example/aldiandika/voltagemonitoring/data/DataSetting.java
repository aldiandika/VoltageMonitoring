package com.example.aldiandika.voltagemonitoring.data;

public class DataSetting {

    private String flagSensor;
    private int const_vr;
    private int const_vs;
    private int const_vt;

    public String getFlagSensor(){
        return this.flagSensor;
    }

    public void setFlagSensor(String flagSensor){
        this.flagSensor = flagSensor;
    }

    public void setConst_vr(int const_vr){
        this.const_vr = const_vr;
    }

    public int getConst_vr(){
        return this.const_vr;
    }

    public void setConst_vs(int const_vs){
        this.const_vs = const_vs;
    }

    public int getConst_vs(){
        return this.const_vs;
    }

    public void setConst_vt(int const_vt){
        this.const_vt = const_vt;
    }

    public int getConst_vt(){
        return this.const_vt;
    }

}
