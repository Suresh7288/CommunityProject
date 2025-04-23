package com.java.communityproject.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class StripeService {
    @Value("${stripe.secret-key}")
    private String secretKey;
    @PostConstruct
    public void init(){
        Stripe.apiKey=secretKey;
    }
    public PaymentIntent createPaymentIntent(long amount, String currency) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount * 100) // Convert to cents
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .build();
        return PaymentIntent.create(params);
    }
}