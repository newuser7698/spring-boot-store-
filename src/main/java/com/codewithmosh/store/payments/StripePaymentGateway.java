package com.codewithmosh.store.payments;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.PaymentStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecret;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            //1- create a parameters to pass to the check out
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT) // set the mode to payment there is another mode for subscription if your app subscribe
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId()) // we set the success URL -> the base url changes so it's better to store it on config file
                    .setCancelUrl(websiteUrl + "/checkout-cancel") // we set the cancel url
                    .setPaymentIntentData(createPaymentInentData(order)); // we add the order id to the stripe we we gwt it in the checkout
            // after creating the builder we add items to this builder
            // for each item we add a stripe line item
            order.getItems().forEach(item -> {
                var lineItem = createLineItem(item, builder);
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build()); // create the session

            return new CheckoutSession(session.getUrl());
        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
            throw new PaymentException("Error creating a checkout session");
        }
    }

    private SessionCreateParams.PaymentIntentData createPaymentInentData(Order order) {
        return SessionCreateParams.PaymentIntentData.builder().putMetadata("order-id", order.getId().toString()).build();
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            // this code takes a webhook request -> { orderId, PaymentStatus }
            var payload = request.getPayload();
            var signature = request.getHeaders().get("Stripe-Signature");

            var event = Webhook.constructEvent(payload, signature, webhookSecret);

            System.out.println("🌀"+event.getType());
            // based on the type we change the stripeObject to
            // there is not one type return

            return switch (event.getType()) {
                case "payment_intent.succeeded" ->
                    // update the order to paid
                    // we sent the id to the stripe as meta data so we get it here
                    // we do not define what is the type of throw ???
                        Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));

                case "payment_intent.payment_failed" ->
                    // update the order to failed
                        Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));

                default -> Optional.empty();

            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Signature");
        }

    }

    private Long extractOrderId(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentException("Could not deserialize Stripe event. Check the SDK and API version.")
        );
        var paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item, SessionCreateParams.Builder builder) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100))) // when we add this item we add it in cents so we multiply with 100
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }
}
