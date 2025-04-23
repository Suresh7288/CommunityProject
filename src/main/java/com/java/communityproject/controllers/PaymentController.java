package com.java.communityproject.controllers;

import com.java.communityproject.services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/payments")
public class PaymentController {
    @Autowired
    private StripeService stripeService;
    @PostMapping("/pay")
    public ResponseEntity<?> createPaymentIntent(@RequestParam Long amount, @RequestParam String currency) throws StripeException {
        try {
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, currency);
            return ResponseEntity.ok(Map.of("clientSecret", paymentIntent.getClientSecret()));
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
