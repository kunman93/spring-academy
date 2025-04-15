package accounts.client;

import common.money.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.net.URI;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * You have to run the application first before you run the tests, since you are using `RestTemplate`
 */
public class AccountClientTests {

    private static final String BASE_URL = "http://localhost:8080";

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Test
    public void listAccounts() {
        // act
        Account[] accounts = restTemplate.getForObject(BASE_URL + "/accounts", Account[].class);

        // assert
        assertNotNull(accounts);
        assertTrue(accounts.length >= 21);
        assertEquals("Keith and Keri Donald", accounts[0].getName());
        assertEquals(2, accounts[0].getBeneficiaries().size());
        assertEquals(Percentage.valueOf("50%"), accounts[0].getBeneficiary("Annabelle").getAllocationPercentage());
    }

    @Test
    public void getAccount() {
        // act
        Account account = restTemplate.getForObject(BASE_URL + "/accounts/{accountId}", Account.class, 0);

        // assert
        assertNotNull(account);
        assertEquals("Keith and Keri Donald", account.getName());
        assertEquals(2, account.getBeneficiaries().size());
        assertEquals(Percentage.valueOf("50%"), account.getBeneficiary("Annabelle").getAllocationPercentage());
    }

    @Test
    public void createAccount() {
        // arrange: use a unique number to avoid conflicts
        String number = String.format("12345%4d", random.nextInt(10000));
        Account account = new Account(number, "John Doe");
        account.addBeneficiary("Jane Doe");

        // act: create a new Account
        URI newAccountLocation = restTemplate.postForLocation(BASE_URL + "/accounts", account);

        // assert: retrieve the Account you just created from the location that was returned.
        Account retrievedAccount = restTemplate.getForObject(Objects.requireNonNull(newAccountLocation), Account.class);

        assertEquals(account.getNumber(), Objects.requireNonNull(retrievedAccount).getNumber());

        Beneficiary accountBeneficiary = account.getBeneficiaries().iterator().next();
        Beneficiary retrievedAccountBeneficiary = retrievedAccount.getBeneficiaries().iterator().next();

        assertEquals(accountBeneficiary.getName(), retrievedAccountBeneficiary.getName());
        assertNotNull(retrievedAccount.getEntityId());
    }

    @Test
    public void addAndDeleteBeneficiary() {
        // perform both add and delete to avoid issues with side effects
        URI newBeneficiaryLocation = restTemplate.postForLocation(BASE_URL + "/accounts/{accountId}/beneficiaries", "David", 1);

        Beneficiary newBeneficiary = restTemplate.getForObject(newBeneficiaryLocation, Beneficiary.class);

        assertNotNull(newBeneficiary);
        assertEquals("David", newBeneficiary.getName());

        restTemplate.delete(newBeneficiaryLocation);


        HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
            System.out.println("You SHOULD get the exception \"No such beneficiary with name 'David'\" in the server.");
            restTemplate.getForObject(newBeneficiaryLocation, Beneficiary.class);
        });
        assertEquals(HttpStatus.NOT_FOUND, httpClientErrorException.getStatusCode());
    }

}
