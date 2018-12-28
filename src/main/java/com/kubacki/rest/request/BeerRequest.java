package com.kubacki.rest.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BeerRequest {

    private String name;
    private String brewery;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("beer name", getName()).
                append("beer brewery", getBrewery()).
                toString();
    }
}
