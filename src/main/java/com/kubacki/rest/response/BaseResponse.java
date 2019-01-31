package com.kubacki.rest.response;

public class BaseResponse {
    private String message;

    public void setPayload(String message) {
        this.message = message;
    }

    public String getPayload() {
        return message;
    }
}
