package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import rewards.internal.monitor.MonitorFactory;
import rewards.internal.monitor.jamon.JamonMonitorFactory;

/**
 * Add @EnableAspectJAutoProxy to this class to instruct Spring
 * to process beans that have the @Aspect annotation.
 * (Note that this annotation is redundant for Spring Boot     -
 * application since it will be automatically added through
 * auto configuration.)
 */
@Configuration
@ComponentScan(basePackages = {"rewards.internal.aspects"})
@EnableAspectJAutoProxy
public class AspectsConfig {

    @Bean
    public MonitorFactory monitorFactory() {
        return new JamonMonitorFactory();
    }

}
