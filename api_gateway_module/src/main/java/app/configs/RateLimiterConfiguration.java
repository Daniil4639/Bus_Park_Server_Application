package app.configs;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig defaultConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(100)
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return RateLimiterRegistry.of(defaultConfig);
    }

    @Bean(name = "busesRateLimiter")
    public RateLimiter busesRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("busesRateLimiter");
    }

    @Bean(name = "pathsRateLimiter")
    public RateLimiter pathsRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("pathsRateLimiter");
    }

    @Bean(name = "departmentsRateLimiter")
    public RateLimiter departmentsRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("departmentsRateLimiter");
    }

    @Bean(name = "driversRateLimiter")
    public RateLimiter driversRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("driversRateLimiter");
    }

    @Bean(name = "stationsRateLimiter")
    public RateLimiter stationsRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("stationsRateLimiter");
    }

    @Bean(name = "scheduleRateLimiter")
    public RateLimiter scheduleRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("scheduleRateLimiter");
    }
}