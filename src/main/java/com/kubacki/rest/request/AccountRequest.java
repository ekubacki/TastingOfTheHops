package com.kubacki.rest.request;

import java.util.Collections;
import java.util.List;

public class AccountRequest {

    private String firstName;
    private String lastName;
    private String email;
    private List<BeerRequest> beers = Collections.EMPTY_LIST;

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

    public void setBeers(List<BeerRequest> beers) {
        this.beers = beers;
    }

    public List<BeerRequest> getBeers() {
        return beers;
    }
}

