package com.kubacki.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Beer {
    private String name;
    private String brewery;
    private String id;
    private transient Map<Integer, Double> yearlyRating;

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

    public Map<Integer, Double> getYearlyRating() {
        return yearlyRating;
    }

    public void setYearlyRating(Map<Integer, Double> yearlyRating) {
        this.yearlyRating = yearlyRating;
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

