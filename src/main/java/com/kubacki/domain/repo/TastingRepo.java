package com.kubacki.domain.repo;

import com.kubacki.domain.Account;
import com.kubacki.domain.Beer;
import com.kubacki.domain.Tasting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TastingRepo extends JdbcTemplate {

    private static final String ACCOUNT_INSERT_SQL = "Insert into ACCOUNTS (account_id,first_name,last_name, email) "//
            + " values (?,?,?,?)";
    private static final String BEER_INSERT_SQL = "Insert into BEERS (beer_id, name, brewery) "//
            + " values (?,?,?)";
    private static final String TASTING_INSERT_SQL = "Insert into TASTINGS (beer_id, account_id, year, tasted, rating) "
            + " values(?,?,?,?,?)";
    private static final String INSERT_LINEUP = "Insert into lineup (beer_id) "
            + " values(?)";

    private static final String FIND_ACCOUNT = "select * from accounts where email = ? or first_name = ? and last_name = ?";
    private static final String GET_ACCOUNT = "select * from accounts where account_id = ?";
    private static final String FIND_BEER = "select * from beers where name = ? and brewery = ?";
    private static final String GET_BEER = "select * from beers where beer_id = ?";
    private static final String GET_TASTINGS_FOR_YEAR = "select * from tastings where year = ?";
    private static final String GET_TASTING_LINEUP = "select beer_id from lineup";
    private static final String GET_RANDOM_TASTINGS = "select beer_id from tastings where tasted = false and year = ? order by rand() limit ?";
    private static final String GET_TASTINGS_FOR_BEER = "select * from tastings where beer_id = ?";
    private static final String DELETE_ALL_LINEUPS = "delete from lineup";
    private static final String UPDATE_TASTINGS_TASTED = "update tastings set tasted = true where beer_id = ?";
    private static final String DELETE_LINEUP = "delete from lineup where beer_id = ?";

    @Autowired
    public TastingRepo(DataSource dataSource) {
        super(dataSource);
    }

    public String saveAccount(Account account) {
        Object[] params = new Object[] { account.getId(), account.getFirstName(), account.getLastName(), account.getEmail()};
        this.update(ACCOUNT_INSERT_SQL, params);
        return account.getId();
    }

    public Account findAccount(String firstName, String lastName, String email) {
        Object[] params = new Object[] { email, firstName, lastName};
        List<Account> accounts = this.query(FIND_ACCOUNT, params, new AccountMapper());
        if(accounts.isEmpty()) {
            return null;
        }
        return accounts.get(0);
    }

    public Account getAccount(String accountId) {
        Object[] params = new Object[] {accountId};
        return this.queryForObject(GET_ACCOUNT, params, new AccountMapper());
    }

    public String addBeer(Beer beer) {
        Object[] params = new Object[] { beer.getId(), beer.getName(), beer.getBrewery()};
        this.update(BEER_INSERT_SQL, params);
        return beer.getId();
    }

    public void addTasting(Tasting tasting) {
        Object[] params = new Object[] { tasting.getBeerId(),
                                         tasting.getAccountId(),
                                         tasting.getYear(),
                                         tasting.isTasted(),
                                         tasting.getRating()
                                       };
        this.update(TASTING_INSERT_SQL, params);
    }

    public Beer findBeer(Beer beer) {
        Object[] params = new Object[] {beer.getName(), beer.getBrewery()};
        List<Beer> beers = this.query(FIND_BEER, params, new BeerMapper());
        if(beers.isEmpty()) {
            return null;
        }
        return beers.get(0);
    }

    public Beer getBeer(String beerId) {
        Object[] params = new Object[] {beerId};
        return this.queryForObject(GET_BEER, params, new BeerMapper());
    }

    public List<Tasting> getAllTastingsForYear(int year) {
        Object[] params = new Object[] {year};
        return this.query(GET_TASTINGS_FOR_YEAR, params, new TastingMapper());
    }

    public List<String> getTastingLineup() {
        return this.queryForList(GET_TASTING_LINEUP, String.class);
    }

    public List<String> getTastings(int numberOfNewTastings, int year) {
        Object[] params = new Object[] {year, numberOfNewTastings};
        return this.queryForList(GET_RANDOM_TASTINGS, params, String.class);
    }

    public void updateLineup(List<String> beerLineup) {
        this.update(DELETE_ALL_LINEUPS);
        for(String beerId : beerLineup) {
            Object[] params = new Object[] {beerId};
            this.update(INSERT_LINEUP, params);
        }
    }

    public List<Tasting> getTastingsForBeers(List<String> beerLineup) {
        List<Tasting> tastings = new ArrayList<Tasting>();
        for(String beerId : beerLineup) {
            Object[] params = new Object[] {beerId};
            tastings.addAll(this.query(GET_TASTINGS_FOR_BEER, params, new TastingMapper()));
        }
        return tastings;
    }

    public void beerTasted(Beer foundBeer) {
        Object[] params = new Object[] {foundBeer.getId()};
        this.update(UPDATE_TASTINGS_TASTED, params);
    }

    public void removeFromLineup(Beer foundBeer) {
        Object[] params = new Object[] {foundBeer.getId()};
        this.update(DELETE_LINEUP, params);
    }

    public static class AccountMapper implements RowMapper<Account> {

        @Override
        public Account mapRow(ResultSet resultSet, int i) throws SQLException {

            Account account = new Account(resultSet.getString("first_name"), resultSet.getString("last_name"));
            account.setId(resultSet.getString("account_id"));
            account.setEmail(resultSet.getString("email"));
            return account;
        }
    }

    public static class BeerMapper implements RowMapper<Beer> {

        @Override
        public Beer mapRow(ResultSet resultSet, int i) throws SQLException {
            Beer beer = new Beer(resultSet.getString("name"), resultSet.getString("brewery"));
            beer.setId(resultSet.getString("beer_id"));
            return beer;
        }
    }

    public static class TastingMapper implements RowMapper<Tasting> {

        @Override
        public Tasting mapRow(ResultSet resultSet, int i) throws SQLException {
            Tasting tasting = new Tasting(resultSet.getString("account_id"), resultSet.getString("beer_id"),
                    resultSet.getInt("year"));
            tasting.setTasted(resultSet.getBoolean("tasted"));
            tasting.setRating(resultSet.getInt("rating"));
            return tasting;
        }
    }
}
