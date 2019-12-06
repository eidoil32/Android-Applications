package com.idohayun.mybusiness;

public class DateArray {
    private int day;
    private int month;
    private int year;
    private int hour;
    private int min;
    private int personID;
    private int userID;
    private int type;
    private int approved;
    private boolean available;

//    DateArray(int day, int month, int year, int hour, int min, int type, int personID, boolean available, int userID) {
//        this.day = day;
//        this.month = month;
//        this.year = year;
//        this.hour = hour;
//        this.min = min;
//        this.personID = personID;
//        this.available = available;
//        this.userID = userID;
//        this.type = type;
//    }

    DateArray(int day, int month, int year, int hour, int min, int type, int personID, boolean available, int userID, int approved) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.min = min;
        this.personID = personID;
        this.available = available;
        this.userID = userID;
        this.type = type;
        this.approved = approved;
    }

    public String toString() {
        return ("Day: " + day +
                "Month: " + month +
                "Year: " + year +
                "Hour: " + hour +
                "Min: " + min);
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void resetDate() {
        this.setUserID(0);
        this.setAvailable(true);
        this.setType(-1);
    }
}
