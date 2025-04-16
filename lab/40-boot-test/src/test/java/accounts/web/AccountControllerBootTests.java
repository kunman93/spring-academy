package accounts.web;

import accounts.AccountManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rewards.internal.account.Account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// Slice Testing: Spring will create a reduced application context for a specific slice of your app.
@WebMvcTest(AccountController.class) // includes @ExtendWith(SpringExtension.class)
public class AccountControllerBootTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountManager accountManager;

    @Test
    public void accountDetails() throws Exception {
        // arrange
        given(accountManager.getAccount(0L))
            .willReturn(new Account("1234567890", "John Doe"));

        // act, assert
        mockMvc.perform(get("/accounts/0"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("name").value("John Doe"))
            .andExpect(jsonPath("number").value("1234567890"));

        // verify
        verify(accountManager).getAccount(0L);
    }

    @Test
    public void accountDetailsFail() throws Exception {
        // arrange
        given(accountManager.getAccount(any(Long.class)))
            .willThrow(new IllegalArgumentException("No such account with id " + 0L));

        // act, assert
        mockMvc.perform(get("/accounts/9999"))
            .andExpect(status().isNotFound());

        // verify
        verify(accountManager).getAccount(any(Long.class));
    }

    @Test
    public void createAccount() throws Exception {
        // arrange
        final Account testAccount = new Account("1234512345", "Mary Jones");
        testAccount.setEntityId(21L);

        given(accountManager.save(any(Account.class)))
            .willReturn(testAccount);

        // act, assert
        mockMvc.perform(
                post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(testAccount))
            )
            .andDo(print()) // use for debugging purposes
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/accounts/21"));

        // verify
        verify(accountManager).save(any(Account.class));

    }

    // Utility class for converting an object into JSON string
    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
