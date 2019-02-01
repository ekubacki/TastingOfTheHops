package com.kubacki.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    private String message;

    public void setPayload(String message) {
        this.message = message;
    }

    public String getPayload() {
        return message;
    }
}
