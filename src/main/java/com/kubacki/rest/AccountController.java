package com.kubacki.rest;

import com.kubacki.domain.Account;
import com.kubacki.domain.Beer;
import com.kubacki.domain.TastingService;
import com.kubacki.rest.request.AccountRequest;
import com.kubacki.rest.request.AddBeerRequest;
import com.kubacki.rest.request.BeerRequest;
import com.kubacki.rest.request.FindAccountRequest;
import com.kubacki.rest.response.AccountCreateResponse;
import com.kubacki.rest.response.BaseResponse;
import com.kubacki.rest.response.FoundAccountResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private TastingService service;

    private static final Logger log = Logger.getLogger(AccountController.class);

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public AccountCreateResponse create(@RequestBody AccountRequest request) {
        AccountCreateResponse response = new AccountCreateResponse();
        String createdAccount;

        try {
            Account account = new Account(request.getFirstName(), request.getLastName());
            account.setEmail(request.getEmail());
            createdAccount = service.createAccount(account);

            for(BeerRequest beerRequest : request.getBeers()) {
                Beer beer = new Beer(beerRequest.getName(), beerRequest.getBrewery());
                String createdBeer = service.addBeer(beer);
                service.addTasting(createdAccount, createdBeer, Calendar.getInstance().get(Calendar.YEAR));
            }
        } catch (IllegalArgumentException e) {
            log.error("handling error", e);
            response.setCode(400);
            response.setPayload(e.getMessage());
            return response;
        } catch (IllegalStateException e) {
            log.error("handling error", e);
            response.setCode(409);
            response.setPayload(e.getMessage());
            return response;
        }

        response.setAccountId(createdAccount);
        response.setCode(200);
        return response;
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public FoundAccountResponse find(@RequestBody FindAccountRequest request) {

        Account account = service.findAccount(request.getAccountId(), request.getFirstName(),
                    request.getLastName(), request.getEmail());
        FoundAccountResponse response = new FoundAccountResponse();

        if (account == null) {
            log.info("account was not found: " + request);
            response.setCode(404);
            response.setPayload("The account was not found");
            return response;
        }

        response.setId(account.getId());
        response.setFirstName(account.getFirstName());
        response.setLastName(account.getLastName());
        response.setEmail(account.getEmail());
        response.setCode(200);
        return response;
    }

    @RequestMapping(value = "/beer", method = RequestMethod.POST)
    public BaseResponse addBeer(@RequestBody AddBeerRequest request) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);

        if(request.getBeers().size() == 0) {
            log.info("add beer request was made with no beers: " + request);
            response.setCode(400);
            response.setPayload("Must add a beer");
            return response;
        }

        Account foundAccount = service.findAccount(request.getAccountId(), request.getFirstName(), request.getLastName(), request.getEmail());

        if (foundAccount == null) {
            log.info("account was not found: " + request);
            response.setCode(404);
            response.setPayload("The account was not found");
            return response;
        }

        for(BeerRequest beerRequest : request.getBeers()) {
            Beer beer = new Beer(beerRequest.getName(), beerRequest.getBrewery());
            String createdBeer = service.addBeer(beer);
            service.addTasting(foundAccount.getId(), createdBeer, Calendar.getInstance().get(Calendar.YEAR));
        }

        return response;
    }
}
