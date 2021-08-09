package com.example.byebuy;

public class Product {
    private String title;
    private String detail;
    private String date;
    private String price;
    private String category;
    private String status;
    private String image;
    private String seller; // selling, complete, reservation
    private String buyer;



    private String reserver; // 예약자

    private String place;

    private String estiStatus; // 평가 상태


    private String placestatus; // "판매자 물건 넣음", "구매자 물건 가져감", "x"

    private String unique;
    private int count;


    public Product(){}


    public Product(String title, String detail, String price,String image,
                   int count,String unique,String date,String seller,
                   String status,String estiStatus,String category,String place,String placestatus) {
        this.title = title;
        this.detail = detail;
        this.price = price;
        this.image = image;
        this.unique= unique;
        this.date = date;
        this.seller = seller;
        this.status = status;
        this.estiStatus = estiStatus;
        this.category = category;
        this.count = count;
        this.place = place;
        this.placestatus =placestatus;
    }


    public String getPlacestatus() {
        return placestatus;
    }

    public void setPlacestatus(String placestatus) {
        this.placestatus = placestatus;
    }
    public String getReserver() {
        return reserver;
    }

    public void setReserver(String reserver) {
        this.reserver = reserver;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public String getEstiStatus() {
        return estiStatus;
    }

    public void setEstiStatus(String estiStatus) {
        this.estiStatus = estiStatus;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }


}
