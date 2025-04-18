package accounts.web;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 *  Access `localhost:8080/actuator/metrics/account.fetch?tag=type:fromAspect`
 */
@Aspect
@Component
public class AccountAspect {
    private final Counter counter;

    public AccountAspect(MeterRegistry meterRegistry) {
        // custom actuator counter metric "account.fetch" which can be accessed through: http://localhost:8080/actuator/metrics/account.fetch
        this.counter = meterRegistry.counter("account.fetch", "type", "fromAspect");
    }

    @Before("execution(* accounts.web.AccountController.accountSummary(..))")
    public void increment() {
        /* the actuator counter metric increases with each fetch.
         * The custom actuator metric "account.fetch" which can be accessed through: http://localhost:8080/actuator/metrics/account.fetch */
        counter.increment();
    }
}
