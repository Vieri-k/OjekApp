package com.example.ojekapp;

public class modelAll {
    String name,phone,region;
    modelAll()
    {

    }
    public modelAll(String name, String phone, String region) {
        this.name = name;
        this.phone = phone;
        this.region = region;
//        this.purl = purl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}


