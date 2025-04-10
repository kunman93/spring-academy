package rewards;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class JdbcBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcBootApplication.class, args);
    }

    // The run method of CommandLineRunner bean gets executed when a Spring Boot application gets started and the ApplicationContext was created.
    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        final var QUERY = "SELECT count(*) FROM T_ACCOUNT";

        // Use Lambda expression to display the result. we return a function, since CommandLineRunner is a functional interface
        return args -> System.out.println("Hello, there are "
            + jdbcTemplate.queryForObject(QUERY, Long.class)
            + " accounts");
    }

}
