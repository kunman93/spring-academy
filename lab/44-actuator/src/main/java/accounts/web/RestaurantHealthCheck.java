package accounts.web;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import rewards.internal.restaurant.RestaurantRepository;


@Component
public class RestaurantHealthCheck implements HealthIndicator {
    private final RestaurantRepository restaurantRepository;

    public RestaurantHealthCheck(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Health health() {
        final var restaurantCount = restaurantRepository.getRestaurantCount();
        return restaurantCount >= 1
            ? Health.up().build()
            : Health.down().build();
    }
}
