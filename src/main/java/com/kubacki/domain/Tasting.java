package com.kubacki.domain;

public class Tasting {
    private String accountId;
    private String beerId;
    private int year;
    private boolean tasted = false; //question is should this be here or on beer;
    private int rating = -1;

    public Tasting(String accountId, String beerId, int year) {
        this.accountId = accountId;
        this.beerId = beerId;
        this.year = year;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBeerId() {
        return beerId;
    }

    public int getYear() {
        return year;
    }

    public boolean isTasted() {
        return tasted;
    }

    public int getRating() {
        return rating;
    }

    public void setTasted(boolean tasted) {
        this.tasted = tasted;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
