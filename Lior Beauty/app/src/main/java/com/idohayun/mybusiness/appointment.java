package com.idohayun.mybusiness;

public class appointment {
    private String description, lang;
    private int price;

    public appointment(String description, String lang, int price) {
        this.description = description;
        this.lang = lang;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
