package rewards;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A system test that demonstrates how propagation settings affect transactional
 * execution.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SystemTestConfig.class})
public class RewardNetworkPropagationTests {

    /**
     * The object being tested.
     */
    @Autowired
    private RewardNetwork rewardNetwork;

    /**
     * A template to use for test verification
     */
    private JdbcTemplate template;

    /**
     * Manages transaction manually
     */
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    public void initJdbcTemplate(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    /* for this test to pass, rewardNetwork.rewardAccountFor(...) should be executed in its own transaction,
     * this can be achieved by annotating the method with `@Transactional(propagation=Propagation.REQUIRES_NEW)`
     */
    @Test
    public void testPropagation() {
        // Open a transaction for testing
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Run the test - generate a reward
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");
        rewardNetwork.rewardAccountFor(dining);

        // Rollback the transaction started by this test
        transactionManager.rollback(status);

        // Assert that a Reward has been saved to the database - for this to be true
        // the RewardNetwork must run and commit its OWN transaction
        String sql = "select SAVINGS from T_ACCOUNT_BENEFICIARY where NAME = ?";
        assertEquals(Double.valueOf(4.00), template.queryForObject(sql, Double.class, "Annabelle"));
        assertEquals(Double.valueOf(4.00), template.queryForObject(sql, Double.class, "Corgan"));
    }
}