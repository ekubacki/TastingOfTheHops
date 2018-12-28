package com.kubacki.domain;

import com.kubacki.domain.repo.TastingRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TastingService {

    @Autowired
    private TastingRepo repo;

    public String createAccount(Account account) {
        Account searchedAccount = repo.findAccount(account.getFirstName(), account.getLastName(), account.getEmail());
        if( searchedAccount == null) {
            return repo.saveAccount(account);
        }
        throw new IllegalStateException("This account already exists: " + searchedAccount.getDisplayName());
    }

    public Account findAccount(String accountId, String firstName, String lastName, String email) {
        if(accountId != null) {
            return repo.getAccount(accountId);
        }
        return repo.findAccount(firstName, lastName, email);
    }

    public String addBeer(Beer beer) {
        Beer searchBeer = repo.findBeer(beer);
        if(searchBeer == null) {
            return repo.addBeer(beer);
        }
        return searchBeer.getId();
    }

    public void addTasting(String accountId, String beerId, int year) {
        Tasting tasting = new Tasting(accountId, beerId, year);
        repo.addTasting(tasting);
    }

    public Map<Beer, List<Account>> getAllTastings() {
        List<Tasting> allTastings = repo.getAllTastingsForYear(Calendar.getInstance().get(Calendar.YEAR));
        return getTastingsListMap(allTastings);
    }

    public Map<Beer, List<Account>> getTastingLineUp() {
        List<String> beerLineup = repo.getTastingLineup();
        if(beerLineup.size() < 3) {
            beerLineup = inflateBeerLineup(beerLineup);
        }
        List<Tasting> tastingLineup = repo.getTastingsForBeers(beerLineup);
        return getTastingsListMap(tastingLineup);
    }

    //TODO: Something's missing about year? maybe?
    //After thinking about it if a beer was not tasted the previous year then this will carpet bomb for the previous
    //years as well?  maybe that's ok?
    public void tastedBeer(Beer beer) {
        Beer foundBeer = repo.findBeer(beer);
        if(foundBeer == null) {
            throw new IllegalStateException("could not find beer");
        }
        repo.removeFromLineup(foundBeer);
        repo.beerTasted(foundBeer);
    }

    private Map<Beer, List<Account>> getTastingsListMap(List<Tasting> allTastings) {
        Map<Beer, List<Account>> tastings = new HashMap<Beer, List<Account>>();
        for (Tasting tasting : allTastings) {
            Account account = repo.getAccount(tasting.getAccountId());

            Beer beer = repo.getBeer(tasting.getBeerId());
            Double rating = repo.getBeerRatingByYear(beer, Calendar.getInstance().get(Calendar.YEAR));
            beer.setYearlyRating(new HashMap<Integer, Double>() {{put(Calendar.getInstance().get(Calendar.YEAR), rating);}});
            List<Account> accounts = tastings.get(beer);
            if (accounts == null) {
                tastings.put(beer, new ArrayList<Account>() {{ add(account); }});
            } else {
                accounts.add(account);
                tastings.put(beer, accounts);
            }
        }
        return tastings;
    }

    private List<String> inflateBeerLineup(List<String> beerLineup) {
        beerLineup = repo.getTastings(3, Calendar.getInstance().get(Calendar.YEAR));
        repo.updateLineup(beerLineup);
        return beerLineup;
    }

    public void rateBeer(Account account, Beer beer, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Must provide rating between 1 and 5");
        }
        Account foundAccount = repo.findAccount(account.getFirstName(), account.getLastName(), account.getEmail());
        if (foundAccount == null) {
            throw new IllegalStateException("This account was not found");
        }
        Beer foundBeer = repo.findBeer(beer);
        if (foundBeer == null) {
            throw new IllegalStateException("This beer was not found");
        }
        repo.addRating(foundAccount, foundBeer, Calendar.getInstance().get(Calendar.YEAR), rating);
    }
}
