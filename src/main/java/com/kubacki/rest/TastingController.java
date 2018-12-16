package com.kubacki.rest;

import com.kubacki.domain.Account;
import com.kubacki.domain.Beer;
import com.kubacki.domain.TastingService;
import com.kubacki.rest.request.BeerRateRequest;
import com.kubacki.rest.request.BeerRequest;
import com.kubacki.rest.response.BaseResponse;
import com.kubacki.rest.response.TastingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tasting")
public class TastingController {

    @Autowired
    private TastingService service;

    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    public BaseResponse rateBeer(BeerRateRequest rateRequest) {
        BaseResponse response = new BaseResponse();
        try {
            service.rateBeer(
                    new Account(rateRequest.getFirstName(), rateRequest.getLastName()),
                    new Beer(rateRequest.getName(), rateRequest.getBrewery()),
                    rateRequest.getRating()
            );
            response.setCode(200);
        } catch (IllegalArgumentException e) {
            response.setCode(400);
            response.setPayload(e.getMessage());
        } catch (IllegalStateException e) {
            response.setCode(404);
            response.setPayload(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value="/tastings", method = RequestMethod.GET)
    public TastingsResponse getTastingList() {
        Map<Beer, List<Account>> allTastings = service.getAllTastings();
        return createTastingsResponse(allTastings);
    }

    @RequestMapping(value="/lineup", method = RequestMethod.GET)
    public TastingsResponse getTastingsLineup() {
        Map<Beer, List<Account>> tastingLineup = service.getTastingLineUp();
        return createTastingsResponse(tastingLineup);
    }

    @RequestMapping(value="/tasted", method = RequestMethod.POST)
    public BaseResponse beerTasted(BeerRequest request) {

        BaseResponse response = new BaseResponse();
        try {
            service.tastedBeer(new Beer(request.getName(), request.getBrewery()));
        } catch (IllegalStateException e) {
            response.setCode(404);
            response.setPayload(e.getMessage());
        }
        return response;
    }

    private TastingsResponse createTastingsResponse(Map<Beer, List<Account>> allTastings) {
        TastingsResponse tastingsResposne = new TastingsResponse();
        for (Map.Entry<Beer, List<Account>> entry : allTastings.entrySet()) {
            Beer beer = entry.getKey();
            List<Account> accountsThatBroughtBeer = entry.getValue();

            TastingsResponse.TastingResponse tastingResponse = new TastingsResponse.TastingResponse();

            tastingResponse.setBeerName(beer.getName());
            tastingResponse.setBrewery(beer.getBrewery());
            tastingResponse.setRating(beer.getYearlyRating().get(Calendar.getInstance().get(Calendar.YEAR)));

            for (Account account : accountsThatBroughtBeer) {
                tastingResponse.addDisplayNames(account.getDisplayName());
            }
            tastingsResposne.addTastingResponse(tastingResponse);
        }
        tastingsResposne.setCode(200);
        return tastingsResposne;
    }

}
