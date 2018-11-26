package com.kubacki.rest.response;

public class BaseResponse {
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setPayload(String message) {
        this.message = message;
    }

    public String getPayload() {
        return message;
    }
}
