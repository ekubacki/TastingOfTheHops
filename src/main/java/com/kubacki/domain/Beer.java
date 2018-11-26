package com.kubacki.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

public class Beer {
    private String name;
    private String brewery;

    private String id;

    public Beer(String name, String brewery) {
        this.id = UUID.randomUUID().toString();
        this.name = mustNotBeEmpty(name, "beer");
        this.brewery = mustNotBeEmpty(brewery, "brewery");
    }

    public String getName() {
        return name;
    }

    public String getBrewery() {
        return brewery;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beer beer = (Beer) o;
        return Objects.equals(name, beer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    private String mustNotBeEmpty(String value, String type) {
        if(StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Must have a value for " + type);
        }
        return value;
    }
}

