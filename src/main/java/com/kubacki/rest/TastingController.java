package com.kubacki.rest;

import com.kubacki.domain.Account;
import com.kubacki.domain.Beer;
import com.kubacki.domain.TastingService;
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
    public BaseResponse rate() {
        throw new UnsupportedOperationException("I haven't completed this yet");
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

    private TastingsResponse createTastingsResponse(Map<Beer, List<Account>> allTastings) {
        TastingsResponse tastingsResposne = new TastingsResponse();
        for (Map.Entry<Beer, List<Account>> entry : allTastings.entrySet()) {
            Beer beer = entry.getKey();
            List<Account> accountsThatBroughtBeer = entry.getValue();

            TastingsResponse.TastingResponse tastingResponse = new TastingsResponse.TastingResponse();

            tastingResponse.setBeerName(beer.getName());
            tastingResponse.setBrewery(beer.getBrewery());

            for (Account account : accountsThatBroughtBeer) {
                tastingResponse.addDisplayNames(account.getDisplayName());
            }
            tastingsResposne.addTastingResponse(tastingResponse);
        }
        return tastingsResposne;
    }
}
