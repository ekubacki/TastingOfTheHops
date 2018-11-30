package com.kubacki.rest.response;

import java.util.ArrayList;
import java.util.List;

public class TastingsResponse extends BaseResponse {

    List<TastingResponse> tastings = new ArrayList<>();

    public void addTastingResponse(TastingResponse tasting) {
        this.tastings.add(tasting);
    }

    public List<TastingResponse> getTastingsResponse() {
        return this.tastings;
    }

    public static class TastingResponse {
        List<String> displayNames = new ArrayList<>();
        String beerName;
        String brewery;
        Double rating;

        public List<String> getDisplayNames() {
            return displayNames;
        }

        public void addDisplayNames(String displayNames) {
            this.displayNames.add(displayNames);
        }

        public String getBeerName() {
            return beerName;
        }

        public void setBeerName(String beerName) {
            this.beerName = beerName;
        }

        public String getBrewery() {
            return brewery;
        }

        public void setBrewery(String brewery) {
            this.brewery = brewery;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }
    }
}