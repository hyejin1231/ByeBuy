package com.example.byebuy;

//포인트 거래 테이블
public class Point {

    public String uid; // 사용자 uid

    public String pointway;  // 충전recharge or 입금 depoist or 송금 send

    public int changepoint; // 포인트
    public String depoister; // 입금받을 사람 uid 받아서 id로 바꿀거임
    public String sender; // 송금하는 사람  uid 받아서 id로 변환하기


    public Point() {
    }

    public Point(String uid, String pointway, int changepoint, String depoister, String sender) {
        this.uid = uid;
        this.pointway = pointway;
        this.changepoint = changepoint;
        this.depoister = depoister;
        this.sender = sender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPointway() {
        return pointway;
    }

    public void setPointway(String pointway) {
        this.pointway = pointway;
    }


    public String getDepoister() {
        return depoister;
    }

    public void setDepoister(String depoister) {
        this.depoister = depoister;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public int getChangepoint() {
        return changepoint;
    }

    public void setChangepoint(int changepoint) {
        this.changepoint = changepoint;
    }
}
