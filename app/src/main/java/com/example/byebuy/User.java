package com.example.byebuy;

public class User {
    public  String comment;
    private String photoUrl;
    private String id;
    private String pw;
    private String nickName;
    private String  warn;


    private int point;



    private int estimate;


    private int estimateUser;

    private  String uid;

    public User(){}

    public User(String photoUrl, String id, String nickName,String uid, String warn, int estimate, int estimateUser,String pw, int point) {
        this.photoUrl = photoUrl;
        this.id = id;
        this.uid = uid;
        this.nickName = nickName;
        this.warn =warn;
        this.estimate = estimate;
        this.estimateUser = estimateUser;
        this.pw = pw;
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getEstimateUser() {
        return estimateUser;
    }

    public void setEstimateUser(int estimateUser) {
        this.estimateUser = estimateUser;
    }

    public int getEstimate() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() { return pw; }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
