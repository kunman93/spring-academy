package accounts.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import rewards.internal.restaurant.JpaRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;

import static org.mockito.Mockito.*;

/* Modify this class to test the RestaurantHealthCheck class.
 * The RestaurantHealthCheck class will implement HealthCheck
 * so a health() method will exist - use it in the tests.
 * Code will not compile until the next step.
 */
public class RestaurantHealthCheckTest {
    private RestaurantHealthCheck restaurantHealthCheck;
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    public void setUp() {
        restaurantRepository = mock(JpaRestaurantRepository.class);
        restaurantHealthCheck = new RestaurantHealthCheck(restaurantRepository);
    }

    @Test
    public void testHealthReturnsUpIfThereAreRestaurants() {
        // Mock the Repository so getRestaurantCount returns 1
        doReturn(1L).when(restaurantRepository).getRestaurantCount();

        // act
        Health result = restaurantHealthCheck.health();

        // Health check should return UP
        verify(restaurantRepository).getRestaurantCount();
        assert (result.getStatus()).equals(Status.UP);
    }

    @Test
    public void testHealthReturnsDownIfThereAreNoRestaurants() {
        // Mock the Repository so getRestaurantCount returns 0
        doReturn(0L).when(restaurantRepository).getRestaurantCount();

        // act
        Health result = restaurantHealthCheck.health();

        // Health check should return DOWN
        verify(restaurantRepository).getRestaurantCount();
        assert (result.getStatus()).equals(Status.DOWN);
    }
}