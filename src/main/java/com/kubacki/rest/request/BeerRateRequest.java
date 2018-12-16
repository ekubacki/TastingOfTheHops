package com.kubacki.rest.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BeerRateRequest extends BeerRequest {
    private String firstName;
    private String lastName;
    private int rating;

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("firstName", firstName).
                append("lastName", lastName).
                append("rating", rating).
                append("beer name", getName()).
                append("beer brewery", getBrewery()).
                toString();
    }
}
