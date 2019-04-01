package com.idohayun.manageapplication;

import static java.lang.Boolean.TRUE;

public class DateListArray {
    private String name, type;
    private int day, month, year, hour, min, phone, personID;
    private boolean available;

    public DateListArray(String name, String type, int day, int month, int year, int hour, int min, int phone, int personID, boolean available) {
        this.name = name;
        this.type = type;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.min = min;
        this.phone = phone;
        this.personID = personID;
        this.available = available;
    }

    @Override
    public String toString() {
        String string = "Name: " + name + " Type: " + type + " Available: " + available + " Phone: " + phone ;
        return string;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void resetDate() {
        this.setName(null);
        this.setAvailable(true);
        this.setType(null);
        this.setPhone(0);
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public String toJSONString() {
        String makeJSONString;
        makeJSONString = "PersonID\n" + personID +
                "Day\n" + day +
                "Month\n" + month +
                "Year\n" + year +
                "Hour\n" + hour +
                "Min\n" + min +
                "FullName\n" + " " +
                "Type\n" + " " +
                "Available\n" + TRUE +
                "Phone" + "0";
        return makeJSONString;
    }
}
