package com.example.aldiandika.voltagemonitoring.data;

public class monitor {

    private String id;
    private String v_satu;
    private String v_dua;
    private String v_tiga;
    private String v_empat;
    private String arus;
    private String daya;

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getArus() {
        return arus;
    }

    public void setArus(String arus) {
        this.arus = arus;
    }

    public String getDaya() {
        return daya;
    }

    public void setDaya(String daya) {
        this.daya = daya;
    }

    public String getV_satu() {
        return v_satu;
    }

    public void setV_satu(String v_satu) {
        this.v_satu = v_satu;
    }

    public String getV_dua() {
        return v_dua;
    }

    public void setV_dua(String v_dua) {
        this.v_dua = v_dua;
    }

    public String getV_tiga() {
        return v_tiga;
    }

    public void setV_tiga(String v_tiga) {
        this.v_tiga = v_tiga;
    }

    public String getV_empat() {
        return v_empat;
    }

    public void setV_empat(String v_empat) {
        this.v_empat = v_empat;
    }
}
