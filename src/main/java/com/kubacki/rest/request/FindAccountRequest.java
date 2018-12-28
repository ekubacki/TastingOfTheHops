package com.kubacki.rest.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FindAccountRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String accountId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("firstName", firstName).
                append("lastName", lastName).
                append("email", email).
                append("account id", accountId).
                toString();
    }
}
