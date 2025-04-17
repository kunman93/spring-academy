package accounts.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AccountServiceMethodSecurityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Disabled
    void getAuthoritiesForUser_should_return_403_for_user() {
        // act
        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth("user", "user")
            .getForEntity("/authorities?username=user", String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Disabled
    void getAuthoritiesForUser_should_return_authorities_for_admin() {
        // act
        String[] authorities = restTemplate.withBasicAuth("admin", "admin")
            .getForObject("/authorities?username=admin", String[].class);
        // assert
        assertThat(authorities.length).isEqualTo(2);
        assertThat(Arrays.toString(authorities).contains("ROLE_ADMIN"));
        assertThat(Arrays.toString(authorities).contains("ROLE_USER"));

    }

    @Test
    public void getAuthoritiesForUser_should_return_authorities_for_superadmin() {
        // act
        String[] authorities = restTemplate.withBasicAuth("superadmin", "superadmin")
            .getForObject("/authorities?username=superadmin", String[].class);

        // assert
        assertThat(authorities.length).isEqualTo(3);
        assertThat(Arrays.toString(authorities).contains("ROLE_ADMIN"));
        assertThat(Arrays.toString(authorities).contains("ROLE_ADMIN"));
        assertThat(Arrays.toString(authorities).contains("ROLE_SUPERADMIN"));
    }

}