package rewards.internal;

import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

/**
 * Rewards an Account for Dining at a Restaurant.
 * <p>
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * <p>
 * Said in other words, this class implements the "reward account for dining" use case.
 * <p>
 * - Understanding internal operations that need to be performed to implement
 * "rewardAccountFor" method of the "RewardNetworkImpl" class
 * - Writing test code using stub implementations of dependencies
 * - Writing both target code and test code without using Spring framework
 */
public class RewardNetworkImpl implements RewardNetwork {
    private final AccountRepository accountRepository;
    private final RestaurantRepository restaurantRepository;
    private final RewardRepository rewardRepository;

    /**
     * Creates a new reward network.
     *
     * @param accountRepository    the repository for loading accounts to reward
     * @param restaurantRepository the repository for loading restaurants that determine how much to reward
     * @param rewardRepository     the repository for recording a record of successful reward transactions
     */
    public RewardNetworkImpl(
        AccountRepository accountRepository,
        RestaurantRepository restaurantRepository,
        RewardRepository rewardRepository
    ) {
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
        this.rewardRepository = rewardRepository;
    }

    public RewardConfirmation rewardAccountFor(Dining dining) {
        final var account = accountRepository.findByCreditCard(dining.getCreditCardNumber());
        final var restaurant = restaurantRepository.findByMerchantNumber(dining.getMerchantNumber());
        final var monetaryAmount = restaurant.calculateBenefitFor(account, dining);
        final var accountContribution = account.makeContribution(monetaryAmount);
        accountRepository.updateBeneficiaries(account);
        return rewardRepository.confirmReward(accountContribution, dining);
    }
}