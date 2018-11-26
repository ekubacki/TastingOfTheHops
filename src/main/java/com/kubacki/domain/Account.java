package com.kubacki.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

public class Account {

    private String id;
    //TODO: Need to lowercase and uppercase first char in the right spots.
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;

    public Account(String firstName, String lastName) {
        this.id = UUID.randomUUID().toString();
        this.firstName = mustNotBeEmpty(firstName, "first name");
        this.lastName = mustNotBeEmpty(lastName, "last name");
        this.displayName = firstName + " " + lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String mustNotBeEmpty(String value, String type) {
        if(StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Must have a value for " + type);
        }
        return value;
    }
}
