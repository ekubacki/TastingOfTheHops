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

            List<Account> accounts = tastings.get(beer);
            if (accounts == null || accounts.isEmpty()) {
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

}
