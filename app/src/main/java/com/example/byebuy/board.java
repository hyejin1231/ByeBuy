package com.example.byebuy;

public class board {
    private String nt_title;
    private String nt_content;
    private String nt_date;
    private String nt_category;
    public board() {
    }

    public board(String nt_title, String nt_content, String nt_date, String nt_category) {
        this.nt_title = nt_title;
        this.nt_content = nt_content;
        this.nt_date = nt_date;
        this.nt_category = nt_category;
    }

    public String getNt_title() {
        return nt_title;
    }

    public void setNt_title(String nt_title) {
        this.nt_title = nt_title;
    }

    public String getNt_content() {
        return nt_content;
    }

    public void setNt_content(String nt_content) {
        this.nt_content = nt_content;
    }

    public String getNt_date() {
        return nt_date;
    }

    public void setNt_date(String nt_date) {
        this.nt_date = nt_date;
    }

    public String getNt_category() {
        return nt_category;
    }

    public void setNt_category(String nt_category) {
        this.nt_category = nt_category;
    }
}
