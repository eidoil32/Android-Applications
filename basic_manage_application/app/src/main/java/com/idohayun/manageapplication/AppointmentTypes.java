package com.idohayun.manageapplication;

class AppointmentTypes {
    private String language, price, description;
    private int id;

    public AppointmentTypes(String language, String price, String description, int id) {
        this.language = language;
        this.price = price;
        this.description = description;
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
