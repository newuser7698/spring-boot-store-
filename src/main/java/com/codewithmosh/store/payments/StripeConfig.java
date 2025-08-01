package com.codewithmosh.store.payments;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${stripe.secretKey}")
    private String secretKey;

    @PostConstruct   // -> With this we tell spring to call this method after the bean is created
    public void init() {
        Stripe.apiKey = secretKey;
    }

}
