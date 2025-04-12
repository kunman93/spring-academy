package rewards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

// TODO-11 (Optional) : Disable 'DataSource' auto-configuration
// - Note that you are using your own 'DataSource' bean now
//   instead of auto-configured one
// - Use 'exclude' attribute of '@SpringBootApplication'
//   excluding 'DataSourceAutoConfiguration' class
// - Run this application and observe a failure
// - Import 'RewardsConfig' class
// - Run this application again and observe a successful execution

// TODO-12 (Optional) : Look in application.properties for the next step.

// TODO-13 (Optional) : Follow the instruction in the lab document.
//           The section titled "Build and Run using Command Line tools".
@SpringBootApplication
@EnableConfigurationProperties(RewardsRecipientProperties.class)
@ConfigurationPropertiesScan
public class RewardsApplication {
    static final String SQL = "SELECT count(*) FROM T_ACCOUNT";

    final Logger logger
        = LoggerFactory.getLogger(RewardsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        final var numberOfAccounts = jdbcTemplate.queryForObject(SQL, Long.class);
        return args -> logger.info("Hello, there are {} accounts", numberOfAccounts);
    }

    @Bean
    CommandLineRunner commandLineRunner2(RewardsRecipientProperties rewardsRecipientProperties) {
        return args -> System.out.println("Recipient: " + rewardsRecipientProperties.getName());
    }

    // TODO-07 (Optional): Enable full debugging in order to observe how Spring Boot
    //           performs its auto-configuration logic
    // - Follow TO-DO-07 in application.properties, then come back here.
    // - Run the application
    // - In the console output, find "CONDITIONS EVALUATION REPORT".
    //   It represents the auto-configuration logic used by Spring Boot.
    // - Search for "JdbcTemplateAutoConfiguration matched:" and
    //   "DataSourceAutoConfiguration matched:". Note that each @Conditional*
    //   represents a single conditional statement in the "JdbcTemplateAutoConfiguration"
    //   and "DataSourceAutoConfiguration" classes.

}
