package com.kubacki.rest.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserRatingRequest {
    private String userId;
    private String beerId;

    public UserRatingRequest() {}

    public UserRatingRequest(String userId, String beerId) {
        this.userId = userId;
        this.beerId = beerId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("userId", getUserId()).
            append("beerId", getBeerId()).
            toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBeerId() {
        return beerId;
    }

    public void setBeerId(String beerId) {
        this.beerId = beerId;
    }
}
