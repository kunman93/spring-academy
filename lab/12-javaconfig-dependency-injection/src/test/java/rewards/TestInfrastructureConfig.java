package rewards;

import config.RewardsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

/**
 * Serves as the master configuration class for RewardNetworkTests. It references to the RewardsConfig.class through the `@Import` statement.
 */
@Configuration
@Import({RewardsConfig.class})
public class TestInfrastructureConfig {

    /**
     * Creates an in-memory "rewards" database populated
     * with test data for fast testing
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            // the scripts can be found in 'src/main/resources/rewards/testdb' directory of the '00-rewards-common' project
            .addScript("classpath:rewards/testdb/schema.sql")
            .addScript("classpath:rewards/testdb/data.sql") //
            .build();
    }
}
