package com.idohayun.manageapplication;

class Treatment {
    private String language, price, description_Hebrew, description_English;
    private int id;

    public String getDescription_English() {
        return description_English;
    }

    public void setDescription_English(String description_English) {
        this.description_English = description_English;
    }

    public Treatment(String language, String price, String description_Hebrew, String description_English ,int id) {
        this.language = language;
        this.price = price;
        this.description_Hebrew = description_Hebrew;
        this.description_English = description_English;
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

    public String getDescription_Hebrew() {
        return description_Hebrew;
    }

    public void setDescription_Hebrew(String description_Hebrew) {
        this.description_Hebrew = description_Hebrew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
