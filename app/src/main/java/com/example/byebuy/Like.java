package com.example.byebuy;

public class Like {

    private String id; //사용자의 고유 id
    private String unique; //제품의 고유 id

    public Like(){}

    public Like(String id, String unique) {
        this.id = id;
        this.unique = unique;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }
}
