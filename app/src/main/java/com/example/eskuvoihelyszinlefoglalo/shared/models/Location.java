package com.example.eskuvoihelyszinlefoglalo.shared.models;

import java.util.Vector;

public class Location {

    private String address;
    private String city;
    private String description;

    private Vector<Object> images;

    private String owner;

    private String name;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector<Object> getImages() {
        return images;
    }

    public void setImages(Vector<Object> images) {
        this.images = images;
    }
}
