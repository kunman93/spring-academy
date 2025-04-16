package accounts.client;

import common.money.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.net.URI;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountClientTests {

    @Autowired
    private TestRestTemplate restTemplate; // when using `RestTemplate`, the application had to be started beforehand. The tests would have failed otherwise

    /**
     * server URL ending with the servlet mapping on which the application can be
     * reached.
     */
    private final Random random = new Random();

    @Test
    public void listAccounts() {
        String relativePath = "/accounts"; // the base url is only needed for RestTemplate, TestRestTemplate only needs the relative path
        // we have to use Account[] instead of List<Account>, or Jackson won't know what
        // type to unmarshal to
        Account[] accounts = restTemplate.getForObject(relativePath, Account[].class);
        assertThat(accounts.length >= 21).isTrue();
        assertThat(accounts[0].getName()).isEqualTo("Keith and Keri Donald");
        assertThat(accounts[0].getBeneficiaries().size()).isEqualTo(2);
        assertThat(accounts[0].getBeneficiary("Annabelle").getAllocationPercentage()).isEqualTo(Percentage.valueOf("50%"));
    }

    @Test
    public void getAccount() {
        String relativePath = "/accounts/{accountId}"; // the base url is only needed for RestTemplate, TestRestTemplate only needs the relative path
        Account account = restTemplate.getForObject(relativePath, Account.class, 0);
        assertThat(account.getName()).isEqualTo("Keith and Keri Donald");
        assertThat(account.getBeneficiaries().size()).isEqualTo(2);
        assertThat(account.getBeneficiary("Annabelle").getAllocationPercentage()).isEqualTo(Percentage.valueOf("50%"));
    }

    @Test
    public void createAccount() {
        String relativePath = "/accounts"; // the base url is only needed for RestTemplate, TestRestTemplate only needs the relative path
        // use a random account number to avoid conflict
        String number = String.format("12345%4d", random.nextInt(10000));
        Account account = new Account(number, "John Doe");
        account.addBeneficiary("Jane Doe");
        URI newAccountLocation = restTemplate.postForLocation(relativePath, account);

        Account retrievedAccount = restTemplate.getForObject(newAccountLocation, Account.class);
        assertThat(retrievedAccount.getNumber()).isEqualTo(account.getNumber());

        Beneficiary accountBeneficiary = account.getBeneficiaries().iterator().next();
        Beneficiary retrievedAccountBeneficiary = retrievedAccount.getBeneficiaries().iterator().next();

        assertThat(retrievedAccountBeneficiary.getName()).isEqualTo(accountBeneficiary.getName());
        assertThat(retrievedAccount.getEntityId()).isNotNull();
    }

    @Test
    public void addAndDeleteBeneficiary() {
        // perform both add and delete to avoid issues with side effects
        String addUrl = "/accounts/{accountId}/beneficiaries"; // the base url is only needed for RestTemplate, TestRestTemplate only needs the relative path
        URI newBeneficiaryLocation = restTemplate.postForLocation(addUrl, "David", 1);
        Beneficiary newBeneficiary = restTemplate.getForObject(newBeneficiaryLocation, Beneficiary.class);
        assertThat(newBeneficiary.getName()).isEqualTo("David");

        restTemplate.delete(newBeneficiaryLocation);

        // TestRestTemplate is, by design, fault-tolerant. This means that it does not throw exceptions when an error response (400 or greater) is received.
        final var response = restTemplate.getForEntity(newBeneficiaryLocation, Beneficiary.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
