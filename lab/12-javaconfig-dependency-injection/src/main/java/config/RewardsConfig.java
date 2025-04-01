package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

import javax.sql.DataSource;


@Configuration
public class RewardsConfig {
    private final DataSource dataSource;

    // as it's the only constructor, @Autowired is optional
    public RewardsConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public RewardNetwork rewardNetwork() {
        return new RewardNetworkImpl(
            accountRepository(),
            restaurantRepository(),
            rewardRepository()
        );
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return new JdbcRestaurantRepository(dataSource);
    }

    @Bean
    public RewardRepository rewardRepository() {
        return new JdbcRewardRepository(dataSource);
    }

}
