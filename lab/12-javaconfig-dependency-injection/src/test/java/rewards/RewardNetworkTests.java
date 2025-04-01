package rewards;

import common.money.MonetaryAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * We are testing the same code as RewardNetworkImplTests, but using a different setup.
 * We are using the applicationContext to initialize rewardNetwork.
 */
public class RewardNetworkTests {
    private RewardNetwork rewardNetwork;
    private RewardNetwork rewardNetwork1;
    private RewardNetwork rewardNetwork2;

    @BeforeEach
    void setUp() {
        // creates an application context based on the TestInfrastructureConfig.class. The Spring beans are managed through the application context.
        final ApplicationContext applicationContext = SpringApplication.run(TestInfrastructureConfig.class);

        // ---- alternative ways to initialise rewardNetwork ----

        // -- No need for bean id if type is unique - recommended (use type whenever possible)
        rewardNetwork = applicationContext.getBean("rewardNetwork", RewardNetwork.class);

        // -- Use typed method to avoid casting
        rewardNetwork1 = applicationContext.getBean("rewardNetwork", RewardNetwork.class);

        // -- use bean id, a cast is needed
        rewardNetwork2 = (RewardNetwork) applicationContext.getBean("rewardNetwork");
    }

    @Test
    void testScopeSingleton() {
        assertEquals(rewardNetwork, rewardNetwork1);
        assertEquals(rewardNetwork1, rewardNetwork2);
        assertEquals(rewardNetwork, rewardNetwork2);
    }

    @Test
    void testRewardForDining() {
        // create a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

        // assert the expected reward confirmation results
        assertNotNull(confirmation);
        assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation.getAccountContribution();
        assertNotNull(contribution);

        // the account number should be '123456789'
        assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        assertEquals(MonetaryAmount.valueOf("8.00"), contribution.getAmount());

        // the total contribution amount should have been split into 2 distributions
        assertEquals(2, contribution.getDistributions().size());

        // each distribution should be 4.00 (as both have a 50% allocation)
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Annabelle").getAmount());
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Corgan").getAmount());
    }
}
