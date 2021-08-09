package com.example.byebuy;

public class berry {
    private String box_buyer;
    private String box_id;
    private String box_photourl;
    private String box_seller;
    private int box_signal;

    public berry() {
    }

    public berry(String box_buyer, String box_id, String box_photourl, String box_seller, int box_signal) {
        this.box_buyer = box_buyer;
        this.box_id = box_id;
        this.box_photourl = box_photourl;
        this.box_seller = box_seller;
        this.box_signal = box_signal;
    }

    public String getBox_buyer() {
        return box_buyer;
    }

    public void setBox_buyer(String box_buyer) {
        this.box_buyer = box_buyer;
    }

    public String getBox_id() {
        return box_id;
    }

    public void setBox_id(String box_id) {
        this.box_id = box_id;
    }

    public String getBox_photourl() {
        return box_photourl;
    }

    public void setBox_photourl(String box_photourl) {
        this.box_photourl = box_photourl;
    }

    public String getBox_seller() {
        return box_seller;
    }

    public void setBox_seller(String box_seller) {
        this.box_seller = box_seller;
    }

    public int getBox_signal() {
        return box_signal;
    }

    public void setBox_signal(int box_signal) {
        this.box_signal = box_signal;
    }
}
