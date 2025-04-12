package rewards;

import config.RewardsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Sets up an embedded in-memory HSQL database, primarily for testing.
 */
@Configuration
@Import(RewardsConfig.class)
public class SystemTestConfig {
}
