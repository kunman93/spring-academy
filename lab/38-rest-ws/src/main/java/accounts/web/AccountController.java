package accounts.web;

import accounts.AccountManager;
import common.money.Percentage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.util.HashMap;
import java.util.List;

/**
 * A controller handling requests for CRUD operations on Accounts and their
 * Beneficiaries.
 */
@RestController
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AccountManager accountManager;

    /**
     * Creates a new AccountController with a given account manager.
     */
    @Autowired
    public AccountController(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Provide a list of all accounts.
     */
    @GetMapping(value = "/accounts")
    public List<Account> accountSummary() {
        return accountManager.getAllAccounts();
    }

    /**
     * Provide the details of an account with the given id.
     */
    @GetMapping(value = "/accounts/{id}")
    public Account accountDetails(@PathVariable int id) {
        return retrieveAccount(id);
    }

    /**
     * Creates a new Account, setting its URL as the Location header on the
     * response.
     */
    @PostMapping(value = "/accounts")
    public ResponseEntity<Void> createAccount(@RequestBody Account newAccount) {
        final Account account = accountManager.save(newAccount);
        return entityWithLocation(account.getEntityId());
    }

    /**
     * Return a response with the location of the new resource.
     * <p>
     * Suppose we have just received an incoming URL of, say,
     * <a href="http://localhost:8080/accounts">...</a> and resourceId is "1111".
     * Then the URL of the new resource will be
     * <a href="http://localhost:8080/accounts/1111">...</a>.
     */
    private ResponseEntity<Void> entityWithLocation(Object resourceId) {
        final var entityWithLocation = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .pathSegment("{resourceId}")
            .build(resourceId);
        return ResponseEntity.created(entityWithLocation)
            .build();
    }

    /**
     * Returns the Beneficiary with the given name for the Account with the
     * given id.
     */
    @GetMapping(value = "/accounts/{accountId}/beneficiaries/{beneficiaryName}")
    public Beneficiary getBeneficiary(@PathVariable("accountId") int accountId,
                                      @PathVariable("beneficiaryName") String beneficiaryName) {
        return retrieveAccount(accountId).getBeneficiary(beneficiaryName);
    }

    /**
     * Adds a Beneficiary with the given name to the Account with the given id,
     * setting its URL as the Location header on the response.
     */
    @PostMapping(value = "/accounts/{accountId}/beneficiaries")
    public ResponseEntity<Void> addBeneficiary(
        @PathVariable long accountId,
        @RequestBody String beneficiaryName
    ) {
        accountManager.addBeneficiary(accountId, beneficiaryName);
        return entityWithLocation(beneficiaryName);
    }

    /**
     * Removes the Beneficiary with the given name from the Account with the
     * given id.
     */
    @DeleteMapping(value = "/accounts/{accountId}/beneficiaries/{beneficiaryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBeneficiary(
        @PathVariable long accountId,
        @PathVariable String beneficiaryName
    ) {
        Account account = accountManager.getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("No such account with id " + accountId);
        }
        Beneficiary b = account.getBeneficiary(beneficiaryName);

        // We ought to reset the allocation percentages, but for now we won't
        // bother. If we are removing the only beneficiary or the beneficiary
        // has an allocation of zero we don't need to worry. Otherwise, throw an
        // exception.
        if (account.getBeneficiaries().size() != 1 && (!b.getAllocationPercentage().equals(Percentage.zero()))) {
            // The solution has the missing logic, if you are interested.
            throw new RuntimeException("Logic to rebalance Beneficiaries not defined.");
        }

        accountManager.removeBeneficiary(accountId, beneficiaryName, new HashMap<String, Percentage>());
    }

    /**
     * Maps IllegalArgumentExceptions to a 404 Not Found HTTP status code.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({IllegalArgumentException.class})
    public void handleNotFound(Exception ex) {
        logger.error("Exception is: ", ex);
        // just return empty 404
    }

    /**
     * Maps DataIntegrityViolationException to a 409 Conflict HTTP status code.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public void handleConflict(Exception ex) {
        logger.error("Exception is: ", ex);
        // just return empty 409
    }

    /**
     * Finds the Account with the given id, throwing an IllegalArgumentException
     * if there is no such Account.
     */
    private Account retrieveAccount(long accountId) throws IllegalArgumentException {
        Account account = accountManager.getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("No such account with id " + accountId);
        }
        return account;
    }

}
