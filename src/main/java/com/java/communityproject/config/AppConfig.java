package com.java.communityproject.config;

import com.java.communityproject.services.StripeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public StripeService stripeService() {
        return new StripeService();
    }
}
