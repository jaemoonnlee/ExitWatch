package com.example.bon300_25.exitwatch.beans;

public class Device {
    private int device_id;
    private int mno;
    private int bid;
    private String name;
    private String description;

    public Device(int num1, int num2, String str1, String str2) {
        this.mno = num1;
        this.bid = num2;
        this.name = str1;
        this.description = str2;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public int getMno() {
        return mno;
    }

    public void setMno(int mno) {
        this.mno = mno;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MNO: " + this.mno + ", "
                + "BID: " + this.bid + ", "
                + "NAME: " + this.name + ", "
                + "DESC: " + this.description + ".";
    }
}
