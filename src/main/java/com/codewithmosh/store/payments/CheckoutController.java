package com.codewithmosh.store.payments;

import com.codewithmosh.store.DTO.ErrorDto;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.repositories.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }

    @PostMapping("/webhook")
    public void handleWebhook(
            // we use this to varify that the request came from stripe
            @RequestHeader Map<String, String> signature,
            // json object that stripe sends to us to tell us what happened
            @RequestBody String payload
    ) {
        System.out.println("payload: " + payload);
        System.out.println("signature: " + signature);
        checkoutService.handleWebhookEvent(new WebhookRequest(signature, payload));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(Exception exception) {
        return ResponseEntity.internalServerError().body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getMessage()));
    }
}
