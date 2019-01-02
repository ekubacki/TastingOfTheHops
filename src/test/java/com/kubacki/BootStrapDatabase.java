package com.kubacki;

import com.kubacki.domain.Account;
import com.kubacki.domain.Beer;
import com.kubacki.domain.repo.TastingRepo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


public class BootStrapDatabase extends JdbcTemplate {

    private static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS accounts ("
        + "account_id VARCHAR(50) NOT NULL,"
        + "first_name VARCHAR(50) NOT NULL,"
        + "last_name VARCHAR(50) NOT NULL,"
        + "email VARCHAR(50),"
        + "CONSTRAINT pk_accounts PRIMARY KEY (account_id),"
        + "UNIQUE KEY accounts_unique_index (first_name, last_name)"
        + ");";

    private static final String CREATE_BEERS_TABLE = "CREATE TABLE IF NOT EXISTS beers ("
        + "beer_id VARCHAR(50) NOT NULL,"
        + "name VARCHAR(50) NOT NULL,"
        + "brewery VARCHAR(50) NOT NULL,"
        + "CONSTRAINT pk_beers PRIMARY KEY (beer_id),"
        + "UNIQUE KEY beers_unique_index (name, brewery)"
        + ");";

    private static final String CREATE_TASTINGS_TABLE = "CREATE TABLE IF NOT EXISTS tastings ("
        + "beer_id VARCHAR(50) NOT NULL,"
        + "account_id VARCHAR(50) NOT NULL,"
        + "year int NOT NULL,"
        + "tasted boolean NOT NULL,"
        + "rating int NOT NULL"
        + ");";

    private static final String CREATE_LINEUP_TABLE = "CREATE TABLE IF NOT EXISTS lineup ("
        + "beer_id VARCHAR(50) NOT NULL"
        + ");";

    private static final String CREATE_RATINGS_TABLE = "CREATE TABLE IF NOT EXISTS ratings ("
        + "account_id VARCHAR(50) NOT NULL,"
        + "beer_id VARCHAR(50) NOT NULL,"
        + "year int NOT NULL,"
        + "rating int NOT NULL,"
        + "CONSTRAINT pk_ratings PRIMARY KEY (account_id, beer_id, year)"
        + ");";

    private static final String DROP_ACCOUNTS_TABLE = "DROP TABLE accounts";
    private static final String DROP_BEERS_TABLE = "DROP TABLE beers";
    private static final String DROP_TASTINGS_TABLE = "DROP TABLE tastings";
    private static final String DROP_LINEUP_TABLE = "DROP TABLE lineup";
    private static final String DROP_RATINGS_TABLE = "DROP TABLE ratings";

    private static final String ACCOUNT_INSERT_SQL = "Insert into ACCOUNTS (account_id,first_name,last_name, email) "//
            + " values (?,?,?,?)";
    private static final String GET_BEER = "select * from beers where name = ? and brewery = ?";
    private static final String BEER_INSERT_SQL = "Insert into BEERS (beer_id, name, brewery) "//
            + " values (?,?,?)";
    private static final String UPDATE_TASTING = "update tastings set year = ? where beer_id = ?";

    public BootStrapDatabase(DataSource dataSource) {
        super(dataSource);
    }

    public void setUpTables() {
        this.update(CREATE_ACCOUNTS_TABLE);
        this.update(CREATE_BEERS_TABLE);
        this.update(CREATE_TASTINGS_TABLE);
        this.update(CREATE_LINEUP_TABLE);
        this.update(CREATE_RATINGS_TABLE);
    }

    public void tearDownTables() {
        this.update(DROP_ACCOUNTS_TABLE);
        this.update(DROP_BEERS_TABLE);
        this.update(DROP_TASTINGS_TABLE);
        this.update(DROP_LINEUP_TABLE);
        this.update(DROP_RATINGS_TABLE);
    }

    public void createAccount() {
        this.createAccount(new Account("firstname", "lastname"));
    }

    public void createAccount(String firstName, String lastName) {
        this.createAccount(new Account(firstName, lastName));
    }

    public void createAccount (String firstName, String lastName, String email) {
        Account account = new Account(firstName, lastName) {{setEmail(email);}};
        this.createAccount(account);
    }

    public void createAccount(Account account) {
        this.createAccount(account.getFirstName(), account.getLastName(), account.getEmail(), account.getId());
    }

    public void createAccount (String firstName, String lastName, String email, String accountId) {
        Object[] params = new Object[] { accountId, firstName, lastName, email};
        this.update(ACCOUNT_INSERT_SQL, params);
    }

    public Beer getBeer(String name, String brewery) {
        Object[] params = new Object[] {name, brewery};
        try {
            return this.queryForObject(GET_BEER, params, new TastingRepo.BeerMapper());
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void createBeer(String beerId, String beerName, String beerBrewery) {
        Object[] params = new Object[] { beerId, beerName, beerBrewery};
        this.update(BEER_INSERT_SQL, params);
    }

    public void updateTastingsYear(String beer_id, int year) {
        Object[] params = new Object[] { year, beer_id};
        this.update(UPDATE_TASTING, params);
    }
}