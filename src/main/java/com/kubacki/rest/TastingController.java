package com.kubacki.rest;

import com.kubacki.domain.Account;
import com.kubacki.domain.Beer;
import com.kubacki.domain.TastingService;
import com.kubacki.rest.request.BeerRateRequest;
import com.kubacki.rest.request.BeerRequest;
import com.kubacki.rest.request.UserRatingRequest;
import com.kubacki.rest.response.BaseResponse;
import com.kubacki.rest.response.TastingsResponse;
import com.kubacki.rest.response.UserRatingResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/tasting")
public class TastingController {

    @Autowired
    private TastingService service;

    private static final Logger log = Logger.getLogger(TastingController.class);

    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> rateBeer(@RequestBody BeerRateRequest rateRequest) {
        BaseResponse response = new BaseResponse();

        try {
            service.rateBeer(
                    new Account(rateRequest.getFirstName(), rateRequest.getLastName()),
                    new Beer(rateRequest.getName(), rateRequest.getBrewery()),
                    rateRequest.getRating()
            );
        } catch (IllegalArgumentException e) {
            log.error("handling error for request: " + rateRequest, e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            log.error("handling error for request: " + rateRequest, e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{beerId}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse> deleteTasting(@PathVariable String beerId) {
        BaseResponse response = new BaseResponse();

        try {
            service.deleteTasting(beerId);
        } catch (IllegalArgumentException e) {
            log.error("handling error for delete beer request: " + e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            log.error("handling error for delete beer request: " + e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value="/tastings", method = RequestMethod.GET)
    public ResponseEntity<TastingsResponse> getTastingList() {
        Map<Beer, List<Account>> allTastings = service.getAllTastings();
        return createTastingsResponse(allTastings);
    }

    @RequestMapping(value="/lineup", method = RequestMethod.GET)
    public ResponseEntity<TastingsResponse> getTastingsLineup() {
        Map<Beer, List<Account>> tastingLineup = service.getTastingLineUp();
        return createTastingsResponse(tastingLineup);
    }

    @RequestMapping(value="/tasted", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> beerTasted(@RequestBody BeerRequest request) {
        log.debug(request);
        BaseResponse response = new BaseResponse();
        try {
            service.tastedBeer(new Beer(request.getName(), request.getBrewery()));
        } catch (IllegalStateException e) {
            log.error("The beer was not found: " + request, e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            log.error("handling error: " + request, e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value="/rating/find", method = RequestMethod.POST)
    public ResponseEntity<UserRatingResponse> findUserRating(@RequestBody UserRatingRequest request) {
        log.debug(request);
        UserRatingResponse response = new UserRatingResponse(0);
        try {
            Integer rating = service.findUserRating(request.getUserId(), request.getBeerId());
            return createUserRatingResponse(rating);
        } catch (IllegalStateException e) {
            log.error("The rating was not found: " + request, e);
            response.setPayload(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private ResponseEntity<TastingsResponse> createTastingsResponse(Map<Beer, List<Account>> allTastings) {
        TastingsResponse tastingsResposne = new TastingsResponse();
        for (Map.Entry<Beer, List<Account>> entry : allTastings.entrySet()) {
            Beer beer = entry.getKey();
            List<Account> accountsThatBroughtBeer = entry.getValue();

            TastingsResponse.TastingResponse tastingResponse = new TastingsResponse.TastingResponse();

            tastingResponse.setBeerName(beer.getName());
            tastingResponse.setBrewery(beer.getBrewery());
            tastingResponse.setRating(beer.getYearlyRating().get(Calendar.getInstance().get(Calendar.YEAR)));
            tastingResponse.setId(beer.getId());

            for (Account account : accountsThatBroughtBeer) {
                tastingResponse.addDisplayNames(account.getDisplayName());
            }
            tastingsResposne.addTastingResponse(tastingResponse);
        }
        return ResponseEntity.ok(tastingsResposne);
    }

    private ResponseEntity<UserRatingResponse> createUserRatingResponse(Integer rating) {
        UserRatingResponse userRatingResponse = new UserRatingResponse(rating);
        return ResponseEntity.ok(userRatingResponse);
    }

}
