package com.example.bon300_25.exitwatch.beans;

public class Building {
    private int bid;
    private String bname;
    private String addr_old;
    private String addr_new;
    private String tel;
    private float xaxis;
    private float yaxis;

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getAddr_old() {
        return addr_old;
    }

    public void setAddr_old(String addr_old) {
        this.addr_old = addr_old;
    }

    public String getAddr_new() {
        return addr_new;
    }

    public void setAddr_new(String addr_new) {
        this.addr_new = addr_new;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public float getXaxis() {
        return xaxis;
    }

    public void setXaxis(float xaxis) {
        this.xaxis = xaxis;
    }

    public float getYaxis() {
        return yaxis;
    }

    public void setYaxis(float yaxis) {
        this.yaxis = yaxis;
    }
}
