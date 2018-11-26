package com.kubacki;

import javax.sql.DataSource;

import com.kubacki.domain.Beer;
import com.kubacki.rest.AccountController;
import com.kubacki.rest.TastingController;
import com.kubacki.rest.request.AccountRequest;
import com.kubacki.rest.request.BeerRequest;
import com.kubacki.rest.request.FindAccountRequest;
import com.kubacki.rest.response.AccountCreateResponse;
import com.kubacki.rest.response.FoundAccountResponse;
import com.kubacki.rest.response.TastingsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;


@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class IntegrationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountController accountController;

    @Autowired
    private TastingController tastingController;

    private BootStrapDatabase databaseSupport;

    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String EMAIL = "THIS_IS_AN_EMAIL@DOMAIN.COM";
    private static final String BEER_NAME = "BEER_NAME";
    private static final String BEER_BREWERY = "BREWERY";

    @Before
    public void setUp() {
        databaseSupport = new BootStrapDatabase(dataSource);
        databaseSupport.setUpTables();
    }

    @After
    public void tearDown() {
        databaseSupport.tearDownTables();
    }

    @Test
    public void testCreateUser_returnsAnAccountId() {
        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(FIRST_NAME);
        createRequest.setLastName(LAST_NAME);
        createRequest.setEmail(EMAIL);

        AccountCreateResponse response = accountController.create(createRequest);
        assertThat(response.getAccountId(), is(notNullValue()));
    }

    @Test
    public void testCreateUser_withAllInfo_shouldReturnAllInfo() {
        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(FIRST_NAME);
        createRequest.setLastName(LAST_NAME);
        createRequest.setEmail(EMAIL);

        String createdAccountId = accountController.create(createRequest).getAccountId();

        FindAccountRequest findRequest = new FindAccountRequest();
        findRequest.setFirstName(FIRST_NAME);
        findRequest.setLastName(LAST_NAME);
        FoundAccountResponse response = accountController.find(findRequest);

        assertThat(response.getId(), is(equalTo(createdAccountId)));
        assertThat(response.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(response.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(response.getEmail(), is(equalTo(EMAIL)));
    }

    @Test
    public void testCreateUser_withoutEmail_shouldCreateAccount() {
        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(FIRST_NAME);
        createRequest.setLastName(LAST_NAME);

        String createdAccountId = accountController.create(createRequest).getAccountId();

        FindAccountRequest findRequest = new FindAccountRequest();
        findRequest.setFirstName(FIRST_NAME);
        findRequest.setLastName(LAST_NAME);
        FoundAccountResponse response = accountController.find(findRequest);

        assertThat(response.getId(), is(equalTo(createdAccountId)));
        assertThat(response.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(response.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(response.getEmail(), is(nullValue()));
    }

    @Test
    public void testCreateUser_withoutFirstName_shouldReturn400BadRequest() {
        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName("");
        createRequest.setLastName(LAST_NAME);

        AccountCreateResponse response = accountController.create(createRequest);
        assertThat(response.getCode(), is(equalTo(400)));
        assertThat(response.getPayload(), is(equalTo("Must have a value for first name")));
    }

    @Test
    public void testCreateUser_withoutLastName_shouldReturn400BadRequest() {
        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(FIRST_NAME);
        createRequest.setLastName("");

        AccountCreateResponse response = accountController.create(createRequest);
        assertThat(response.getCode(), is(equalTo(400)));
        assertThat(response.getPayload(), is(equalTo("Must have a value for last name")));
    }

    @Test
    public void testCreateUser_thatAlreadyExists_shouldReturn409UserAlreadyExists() {
        databaseSupport.createAccount(FIRST_NAME, LAST_NAME);

        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(FIRST_NAME);
        createRequest.setLastName(LAST_NAME);

        AccountCreateResponse response = accountController.create(createRequest);
        assertThat(response.getCode(), is(equalTo(409)));
        assertThat(response.getPayload(), is(equalTo("This account already exists: " + FIRST_NAME + " " + LAST_NAME)));
    }

    @Test
    public void testCreateUser_withSameEmailDifferentName_shouldReturn409UserAlreadyExists() {
        databaseSupport.createAccount(FIRST_NAME, LAST_NAME, EMAIL);

        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(FIRST_NAME + "_not the same"  );
        createRequest.setLastName(LAST_NAME + " really it's not");
        createRequest.setEmail(EMAIL);

        AccountCreateResponse response = accountController.create(createRequest);
        assertThat(response.getCode(), is(equalTo(409)));
        assertThat(response.getPayload(), is(equalTo("This account already exists: " + FIRST_NAME + " " + LAST_NAME)));
    }

    @Test
    public void testFindUser_withFirstNameAndLastName_shouldFindUser() {
        databaseSupport.createAccount(FIRST_NAME, LAST_NAME);

        FindAccountRequest request = new FindAccountRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);

        FoundAccountResponse response = accountController.find(request);
        assertThat(response.getId(), is(notNullValue()));
        assertThat(response.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(response.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(response.getEmail(), is(nullValue()));
    }

    @Test
    public void testFindUser_withEmail_shouldFindUser() {
        databaseSupport.createAccount(FIRST_NAME, LAST_NAME, EMAIL);

        FindAccountRequest request = new FindAccountRequest();
        request.setEmail(EMAIL);

        FoundAccountResponse response = accountController.find(request);
        assertThat(response.getId(), is(notNullValue()));
        assertThat(response.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(response.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(response.getEmail(), is(EMAIL));
    }

    @Test
    public void testFindUser_withId_shouldFindUser() {
        databaseSupport.createAccount(FIRST_NAME, LAST_NAME, EMAIL, "ACCOUNT_ID");

        FindAccountRequest request = new FindAccountRequest();
        request.setAccountId("ACCOUNT_ID");

        FoundAccountResponse response = accountController.find(request);
        assertThat(response.getId(), is("ACCOUNT_ID"));
        assertThat(response.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(response.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(response.getEmail(), is(EMAIL));
    }

    @Test
    public void testFindUser_thatDoesNotExist_shouldReturn404NotFound() {
        FindAccountRequest request = new FindAccountRequest();

        FoundAccountResponse response = accountController.find(request);
        assertThat(response.getId(), is(nullValue()));
        assertThat(response.getFirstName(), is(nullValue()));
        assertThat(response.getLastName(), is(nullValue()));
        assertThat(response.getEmail(), is(nullValue()));
        assertThat(response.getCode(), is(equalTo(404)));
        assertThat(response.getPayload(), is(equalTo("The account was not found")));
    }

    @Test
    public void testCreateAccount_withABeer_shouldCreateBeer() {
        AccountRequest request = buildValidUserRequest();

        BeerRequest beerRequest = new BeerRequest();
        beerRequest.setName(BEER_NAME);
        beerRequest.setBrewery(BEER_BREWERY);
        request.setBeers(new ArrayList<BeerRequest>(){{add(beerRequest);}});

        accountController.create(request);

        //TODO: Likely missing a way to get beers without directly digging into the database
        Beer beer = databaseSupport.getBeer(BEER_NAME, BEER_BREWERY);
        assertThat(beer.getId(), is(notNullValue()));
        assertThat(beer.getName(), is(equalTo(BEER_NAME)));
        assertThat(beer.getBrewery(), is(equalTo(BEER_BREWERY)));
    }

    @Test
    public void testCreateAccount_withMoreThanOneBeer_shouldCreateAllBeers() {
        AccountRequest request = buildValidUserRequest();

        BeerRequest beerOne = new BeerRequest(){{
            setName(BEER_NAME);
            setBrewery(BEER_BREWERY);
        }};
        BeerRequest beerTwo = new BeerRequest(){{
            setName(BEER_NAME + "_TWO");
            setBrewery(BEER_BREWERY + "_TWO");
        }};

        request.setBeers(new ArrayList<BeerRequest>(){{
            add(beerOne);
            add(beerTwo);
        }});

        accountController.create(request);

        //TODO: Likely missing a way to get beers without directly digging into the database
        Beer beer = databaseSupport.getBeer(BEER_NAME, BEER_BREWERY);
        assertThat(beer.getId(), is(notNullValue()));
        assertThat(beer.getName(), is(equalTo(BEER_NAME)));
        assertThat(beer.getBrewery(), is(equalTo(BEER_BREWERY)));

        beer = databaseSupport.getBeer(BEER_NAME + "_TWO", BEER_BREWERY + "_TWO");
        assertThat(beer.getId(), is(notNullValue()));
        assertThat(beer.getName(), is(equalTo(BEER_NAME + "_TWO")));
        assertThat(beer.getBrewery(), is(equalTo(BEER_BREWERY + "_TWO")));
    }

    @Test
    public void testCreateAccount_withBeerThatExists_shouldNotRecreateBeer() {
        AccountRequest request = buildValidUserRequest();

        databaseSupport.createBeer("BEER_ID", BEER_NAME, BEER_BREWERY);

        BeerRequest beerRequest = new BeerRequest();
        beerRequest.setName(BEER_NAME);
        beerRequest.setBrewery(BEER_BREWERY);
        request.setBeers(new ArrayList<BeerRequest>(){{add(beerRequest);}});

        accountController.create(request);

        //TODO: Likely missing a way to get beers without directly digging into the database
        Beer beer = databaseSupport.getBeer(BEER_NAME, BEER_BREWERY);
        assertThat(beer.getId(), is("BEER_ID"));
        assertThat(beer.getName(), is(equalTo(BEER_NAME)));
        assertThat(beer.getBrewery(), is(equalTo(BEER_BREWERY)));
    }

    @Test
    public void testCreateAccount_withBeerWithNoName_shouldReturn400() {
        AccountRequest request = buildValidUserRequest();

        BeerRequest beerRequest = new BeerRequest();
        beerRequest.setBrewery(BEER_BREWERY);
        request.setBeers(new ArrayList<BeerRequest>(){{add(beerRequest);}});

        AccountCreateResponse response = accountController.create(request);
        assertThat(response.getCode(), is(equalTo(400)));
        assertThat(response.getPayload(), is(equalTo("Must have a value for beer")));
    }

    @Test
    public void testCreateAccount_withBreweryWithNoName_shouldReturn400() {
        AccountRequest request = buildValidUserRequest();

        BeerRequest beerRequest = new BeerRequest();
        beerRequest.setName(BEER_NAME);
        request.setBeers(new ArrayList<BeerRequest>(){{add(beerRequest);}});

        AccountCreateResponse response = accountController.create(request);
        assertThat(response.getCode(), is(equalTo(400)));
        assertThat(response.getPayload(), is(equalTo("Must have a value for brewery")));
    }

    @Test
    public void testCreateAccount_withABeer_shouldCreateATasting() {
        AccountRequest accountRequest = buildValidUserRequestWithABeer();

        accountController.create(accountRequest);
        TastingsResponse tastingsResponse = tastingController.getTastingList();

        assertThat(tastingsResponse.getTastingsResponse().size() , is(equalTo(1)));
        TastingsResponse.TastingResponse tastingResponse = tastingsResponse.getTastingsResponse().get(0);


        assertThat(tastingResponse.getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingResponse.getDisplayNames().get(0), is(equalTo(FIRST_NAME + " " + LAST_NAME)));

        assertThat(tastingResponse.getBeerName(), is(equalTo(BEER_NAME)));
        assertThat(tastingResponse.getBrewery(), is(equalTo(BEER_BREWERY)));
    }

    @Test
    public void testCreateAccount_withTwoBeers_shouldCreateTwoTastings() {
        AccountRequest accountRequest = buildValidUserRequestWithTwoBeers();

        accountController.create(accountRequest);
        TastingsResponse tastingsResponse = tastingController.getTastingList();

        assertThat(tastingsResponse.getTastingsResponse().size() , is(equalTo(2)));
        assertThat( tastingsResponse.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(1)));
        assertThat( tastingsResponse.getTastingsResponse().get(1).getDisplayNames().size(), is(equalTo(1)));
    }

    @Test
    public void testCreateAccount_withMultiplePeopleBringSameBeer_shouldCreateOneTastings() {
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME,
                EMAIL, BEER_NAME, BEER_BREWERY));
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_2", LAST_NAME + "_2",
                EMAIL + "_2", BEER_NAME, BEER_BREWERY));

        TastingsResponse tastingsResponse = tastingController.getTastingList();

        assertThat(tastingsResponse.getTastingsResponse().size() , is(equalTo(1)));
        assertThat(tastingsResponse.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(2)));
    }

    @Test
    public void testGetTastingLineup_withManyTastings_shouldReturnOnlyThreeTastings() {
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME,
                EMAIL, BEER_NAME, BEER_BREWERY));
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_2", LAST_NAME + "_2",
                EMAIL + "_2", BEER_NAME + "_2", BEER_BREWERY + "_2"));
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_3", LAST_NAME + "_3",
                EMAIL + "_3", BEER_NAME + "_3", BEER_BREWERY + "_3"));
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_4", LAST_NAME + "_4",
                EMAIL + "_4", BEER_NAME + "_4", BEER_BREWERY + "_4"));

        TastingsResponse tastingsLineup = tastingController.getTastingsLineup();

        assertThat(tastingsLineup.getTastingsResponse().size() , is(equalTo(3)));
        assertThat(tastingsLineup.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(1).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(2).getDisplayNames().size(), is(equalTo(1)));
    }

    @Test
    public void testGetTastingLineup_withOneTasting_shouldReturnOnlyOneTasting() {
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME,
                EMAIL, BEER_NAME, BEER_BREWERY));

        TastingsResponse tastingsLineup = tastingController.getTastingsLineup();

        assertThat(tastingsLineup.getTastingsResponse().size() , is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(1)));
    }

    @Test
    public void testGetTastingLineup_withTwoTasting_shouldReturnOnlyTwoTasting() {
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME,
                EMAIL, BEER_NAME, BEER_BREWERY));
        tastingController.getTastingsLineup();
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_2", LAST_NAME + "_2",
                EMAIL + "_2", BEER_NAME + "_2", BEER_BREWERY + "_2"));
        TastingsResponse tastingsLineup = tastingController.getTastingsLineup();

        assertThat(tastingsLineup.getTastingsResponse().size() , is(equalTo(2)));
        assertThat(tastingsLineup.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(1).getDisplayNames().size(), is(equalTo(1)));
    }

    @Test
    public void testGetTastingLineup_withThreeTasting_shouldReturnOnlyThreeTasting() {
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME,
                EMAIL, BEER_NAME, BEER_BREWERY));
        tastingController.getTastingsLineup();
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_2", LAST_NAME + "_2",
                EMAIL + "_2", BEER_NAME + "_2", BEER_BREWERY + "_2"));
        tastingController.getTastingsLineup();

        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_3", LAST_NAME + "_3",
                EMAIL + "_3", BEER_NAME + "_3", BEER_BREWERY + "_3"));
        TastingsResponse tastingsLineup = tastingController.getTastingsLineup();

        assertThat(tastingsLineup.getTastingsResponse().size() , is(equalTo(3)));
        assertThat(tastingsLineup.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(1).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(2).getDisplayNames().size(), is(equalTo(1)));
    }

    @Test
    public void testGetTastingLineup_withThreeTastingAfterTwo_shouldReturnOnlyThreeTasting() {
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME,
                EMAIL, BEER_NAME, BEER_BREWERY));
        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_2", LAST_NAME + "_2",
                EMAIL + "_2", BEER_NAME + "_2", BEER_BREWERY + "_2"));
        tastingController.getTastingsLineup();

        accountController.create(buildValidRequestWithABeerForUser(FIRST_NAME + "_3", LAST_NAME + "_3",
                EMAIL + "_3", BEER_NAME + "_3", BEER_BREWERY + "_3"));
        TastingsResponse tastingsLineup = tastingController.getTastingsLineup();

        assertThat(tastingsLineup.getTastingsResponse().size() , is(equalTo(3)));
        assertThat(tastingsLineup.getTastingsResponse().get(0).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(1).getDisplayNames().size(), is(equalTo(1)));
        assertThat(tastingsLineup.getTastingsResponse().get(2).getDisplayNames().size(), is(equalTo(1)));
    }

    /*
    This is a bit hacky.

    I need a way to validate that getTastings only returns beers that were added this calendar year.  It doesn't make
    sense to push this logic out to an external client to enter.

    The service does not provide a handle at this point to "set" or "retreive" based on year however the idea is that
    the data won't be reset year over year.

    So the only handle is to directly inject the year at the database level.
     */
    @Test
    public void testGetAllTasting_forTastingsNotInSameYear_shouldOnlyReturnCurrentYear() {
        AccountRequest request = buildValidUserRequest(FIRST_NAME, LAST_NAME, EMAIL);

        BeerRequest beerOne = new BeerRequest(){{
            setName(BEER_NAME);
            setBrewery(BEER_BREWERY);
        }};
        BeerRequest beerTwo = new BeerRequest(){{
            setName(BEER_NAME + "_TWO");
            setBrewery(BEER_BREWERY + "_TWO");
        }};

        request.setBeers(new ArrayList<BeerRequest>(){{
            add(beerOne);
            add(beerTwo);
        }});

        accountController.create(request);
        assertThat(tastingController.getTastingList().getTastingsResponse().size(), is(equalTo(2)));
        Beer beer = databaseSupport.getBeer(BEER_NAME, BEER_BREWERY);
        databaseSupport.updateTastingsYear(beer.getId(), 2017); //this is where we cheat the api
        assertThat(tastingController.getTastingList().getTastingsResponse().size(), is(equalTo(1)));
    }
    /*
    This is a bit hacky.

    I need a way to validate that getTastings only returns beers that were added this calendar year.  It doesn't make
    sense to push this logic out to an external client to enter.

    The service does not provide a handle at this point to "set" or "retreive" based on year however the idea is that
    the data won't be reset year over year.

    So the only handle is to directly inject the year at the database level.
     */
    @Test
    public void testGetTastingLineup_forTastingsNotInSameYear_shouldOnlyReturnCurrentYear() {
        AccountRequest request = buildValidUserRequest(FIRST_NAME, LAST_NAME, EMAIL);

        BeerRequest beerOne = new BeerRequest(){{
            setName(BEER_NAME);
            setBrewery(BEER_BREWERY);
        }};
        BeerRequest beerTwo = new BeerRequest(){{
            setName(BEER_NAME + "_TWO");
            setBrewery(BEER_BREWERY + "_TWO");
        }};

        request.setBeers(new ArrayList<BeerRequest>(){{
            add(beerOne);
            add(beerTwo);
        }});

        accountController.create(request);
        assertThat(tastingController.getTastingsLineup().getTastingsResponse().size(), is(equalTo(2)));
        Beer beer = databaseSupport.getBeer(BEER_NAME, BEER_BREWERY);
        databaseSupport.updateTastingsYear(beer.getId(), 2017); //this is where we cheat the api
        assertThat(tastingController.getTastingsLineup().getTastingsResponse().size(), is(equalTo(1)));
    }

    @Ignore
    @Test
    public void testGetTastingLineup_forTastingsThatHaveBeenTasted_shouldNotReturnThem() {
        //this should make me build a "tasted" api;
        fail("Not sure how to test ... .haha");
    }

    @Ignore
    @Test
    public void testRateBeer() {
        fail("This is not yet started");
    }

    private AccountRequest buildValidUserRequestWithABeer() {
        return buildValidRequestWithABeerForUser(FIRST_NAME, LAST_NAME, EMAIL, BEER_NAME, BEER_BREWERY);
    }

    private AccountRequest buildValidRequestWithABeerForUser(String firstName, String lastName, String email,
            String beerName, String beerBrewery) {
        AccountRequest request = buildValidUserRequest(firstName, lastName, email);

        BeerRequest beerRequest = new BeerRequest();
        beerRequest.setName(beerName);
        beerRequest.setBrewery(beerBrewery);
        request.setBeers(new ArrayList<BeerRequest>(){{add(beerRequest);}});
        return request;
    }

    private AccountRequest buildValidUserRequestWithTwoBeers() {
        AccountRequest request = buildValidUserRequest();

        BeerRequest beerOne = new BeerRequest(){{
            setName(BEER_NAME);
            setBrewery(BEER_BREWERY);
        }};
        BeerRequest beerTwo = new BeerRequest(){{
            setName(BEER_NAME + "_TWO");
            setBrewery(BEER_BREWERY + "_TWO");
        }};

        request.setBeers(new ArrayList<BeerRequest>(){{
            add(beerOne);
            add(beerTwo);
        }});
        return request;
    }

    private AccountRequest buildValidUserRequest() {
        return buildValidUserRequest(FIRST_NAME, LAST_NAME, EMAIL);
    }

    private AccountRequest buildValidUserRequest(String firstName, String lastName, String email) {
        AccountRequest createRequest = new AccountRequest();
        createRequest.setFirstName(firstName);
        createRequest.setLastName(lastName);
        createRequest.setEmail(email);
        return createRequest;
    }
}