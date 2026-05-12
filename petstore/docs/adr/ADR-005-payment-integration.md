# ADR-005: Payment Integration - Stripe

**Date**: 2026-05-05  
**Status**: ACCEPTED  
**Author**: Petstore Team  
**Deciders**: Backend Lead, Finance Team

## Context

Petstore MVP needs to process credit card payments for pet purchases. Key requirements:

1. **PCI Compliance**: Never store credit card data
2. **Security**: Industry-standard encryption
3. **Reliability**: 99.9% uptime for payment processing
4. **Scalability**: Handle 100+ transactions per hour
5. **Cost**: Reasonable transaction fees

## Decision

Integrate **Stripe** as payment processor.

## Rationale

### Payment Processor Comparison

| Factor | Stripe | Square | PayPal | Authorize.net |
|--------|--------|--------|--------|---------------|
| PCI Compliance | ✅ Fully managed | ✅ Fully managed | ✅ Fully managed | ⚠️ Shared responsibility |
| API Quality | ✅ Excellent | ✅ Good | ⚠️ Legacy | ⚠️ Dated |
| SDKs | ✅ All languages | ✅ Most | ⚠️ Limited | ⚠️ Limited |
| Webhooks | ✅ Robust | ✅ Good | ⚠️ Limited | ⚠️ Basic |
| Dispute Handling | ✅ Excellent | ✅ Good | ⚠️ Poor | ⚠️ Poor |
| Transaction Fee | 2.9% + $0.30 | 2.6% + $0.30 | 2.9% + $0.30 | 2.9% + $0.30 |
| Setup Time | 1 day | 1 day | 1 day | 2-3 days |
| Marketplace Model | ✅ Supported | ⚠️ Limited | ❌ Not suitable | ❌ Not suitable |

### Why Stripe?

1. **Best-in-class API**: Excellent documentation and SDKs
2. **Marketplace Support**: Built for multi-vendor platforms
3. **Connect Program**: Separate payments for each seller
4. **Webhooks**: Reliable event notifications
5. **Disputes**: Easy chargebacks handling
6. **Dashboard**: Real-time monitoring and analytics
7. **Ecosystem**: Extensive integrations (tax, shipping, etc.)

## Implementation

### Payment Flow

```
1. User adds items to cart
                │
                ▼
2. User clicks "Checkout"
   Frontend redirects to checkout page
                │
                ▼
3. Checkout Page collects:
   ├─ Shipping address
   ├─ Email for confirmation
   └─ Card details (Stripe Elements handles this)
                │
                ▼
4. User submits checkout
   Frontend calls: POST /api/payments/create-intent
                │
                ▼
5. Backend creates Stripe PaymentIntent
   - Amount: Total (including commission)
   - Metadata: order_id, seller_id, buyer_id
   - Return: client_secret
                │
                ▼
6. Frontend confirms payment with client_secret
   Stripe.confirmCardPayment(clientSecret, { payment_method })
                │
                ▼
7. Stripe processes payment
   ├─ If approved: Send webhook
   ├─ If declined: Return error to frontend
   └─ If 3D Secure required: Challenge user
                │
                ▼
8. Backend receives webhook: payment_intent.succeeded
   ├─ Create Order
   ├─ Update inventory
   ├─ Split payment (Seller + Platform)
   ├─ Send confirmation email
   └─ Save payment record
```

### Backend Implementation

```java
@Service
public class PaymentService {
  
  private Stripe stripe;
  
  public CreatePaymentIntentResponse createIntent(CheckoutRequest request) {
    // Calculate amounts
    BigDecimal subtotal = calculateSubtotal(request.items);
    BigDecimal tax = subtotal.multiply(new BigDecimal("0.09"));
    BigDecimal shipping = new BigDecimal("10");
    BigDecimal platformCommission = subtotal.multiply(new BigDecimal("0.12")); // 12% commission
    BigDecimal sellerPayout = subtotal.subtract(platformCommission);
    BigDecimal total = subtotal.add(tax).add(shipping);
    
    // Create Stripe PaymentIntent
    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
      .setAmount(total.multiply(new BigDecimal("100")).longValue()) // Convert to cents
      .setCurrency("usd")
      .setAutomaticPaymentMethods(
        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
          .setEnabled(true)
          .build()
      )
      .putMetadata("order_id", request.orderId.toString())
      .putMetadata("seller_id", request.sellerId.toString())
      .putMetadata("buyer_id", request.buyerId.toString())
      .build();
    
    PaymentIntent paymentIntent = PaymentIntent.create(params);
    
    return new CreatePaymentIntentResponse(
      paymentIntent.getClientSecret(),
      total
    );
  }
  
  @Transactional
  public void handlePaymentSucceeded(String paymentIntentId) {
    PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
    
    // Extract metadata
    Long orderId = Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    Long sellerId = Long.valueOf(paymentIntent.getMetadata().get("seller_id"));
    
    // Create order
    Order order = orderService.createOrder(orderId, sellerId);
    
    // Save payment record
    Payment payment = new Payment();
    payment.setOrder(order);
    payment.setStripePaymentIntentId(paymentIntentId);
    payment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount() / 100.0));
    payment.setStatus("COMPLETED");
    paymentRepository.save(payment);
    
    // Update seller earnings
    sellerEarningService.recordEarning(sellerId, order.getSellerPayout());
    
    // Send confirmation email
    emailService.sendOrderConfirmation(order);
  }
}
```

### Webhook Handler

```java
@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {
  
  @PostMapping
  public ResponseEntity<Void> handleWebhook(
    @RequestBody String payload,
    @RequestHeader("Stripe-Signature") String signature
  ) {
    // Verify webhook signature
    if (!verifyWebhookSignature(payload, signature)) {
      return ResponseEntity.status(401).build();
    }
    
    Event event = Gson.fromJson(payload, Event.class);
    
    switch (event.getType()) {
      case "payment_intent.succeeded":
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        paymentService.handlePaymentSucceeded(paymentIntent.getId());
        break;
        
      case "payment_intent.payment_failed":
        paymentIntent = (PaymentIntent) event.getData().getObject();
        paymentService.handlePaymentFailed(paymentIntent.getId());
        break;
        
      case "charge.dispute.created":
        // Handle chargeback
        break;
    }
    
    return ResponseEntity.ok().build();
  }
  
  private boolean verifyWebhookSignature(String payload, String signature) {
    // Use Stripe SDK to verify
    return Webhook.constructEvent(payload, signature, webhookSecret) != null;
  }
}
```

### Frontend Implementation

```javascript
// pages/Checkout.jsx
import { loadStripe } from '@stripe/js';
import { CardElement, Elements, useStripe, useElements } from '@stripe/react-js';

function CheckoutForm() {
  const stripe = useStripe();
  const elements = useElements();
  const { total, orderId } = useCheckout();
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 1. Get client secret from backend
    const response = await api.post('/payments/create-intent', { orderId });
    const { clientSecret, total } = response.data.data;
    
    // 2. Confirm payment
    const { paymentIntent, error } = await stripe.confirmCardPayment(
      clientSecret,
      {
        payment_method: {
          card: elements.getElement(CardElement),
          billing_details: { email: userEmail }
        }
      }
    );
    
    if (error) {
      // Show error message
      setError(error.message);
    } else if (paymentIntent.status === 'succeeded') {
      // Success! Redirect to order confirmation
      navigate(`/orders/${orderId}`);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <CardElement />
      <button type="submit">Pay ${total}</button>
    </form>
  );
}
```

## Key Features

### Multi-Seller Payment Splitting

```java
// Calculate amounts
BigDecimal platformCommission = total * 0.12;  // 12% to platform
BigDecimal sellerPayout = total - platformCommission;

// Record earnings
SellerEarning earning = new SellerEarning();
earning.setSeller(seller);
earning.setGrossAmount(total);
earning.setCommissionAmount(platformCommission);
earning.setSellerPayout(sellerPayout);
earning.setOrder(order);
sellerEarningRepository.save(earning);
```

### Payout Management

```java
@Service
public class PayoutService {
  
  public Payout requestPayout(Long sellerId, BigDecimal amount) {
    // Create Stripe payout
    PayoutCreateParams params = PayoutCreateParams.builder()
      .setAmount(amount.multiply(new BigDecimal("100")).longValue())
      .setCurrency("usd")
      .setMethod(PayoutCreateParams.Method.INSTANT)
      .build();
    
    com.stripe.model.Payout stripePayout = com.stripe.model.Payout.create(params);
    
    // Save payout record
    Payout payout = new Payout();
    payout.setSeller(sellerRepository.findById(sellerId).get());
    payout.setAmount(amount);
    payout.setStripePayoutId(stripePayout.getId());
    payout.setStatus("PENDING");
    return payoutRepository.save(payout);
  }
}
```

### Dispute Handling

```java
@Service
public class DisputeService {
  
  @Transactional
  public void handleDispute(String chargeId) {
    // Log dispute in audit trail
    Dispute dispute = new Dispute();
    dispute.setStripeChargeId(chargeId);
    dispute.setStatus("OPEN");
    disputeRepository.save(dispute);
    
    // Notify seller
    emailService.sendDisputeNotification(chargeId);
    
    // Charge seller for dispute if customer wins
  }
}
```

## Configuration

### Environment Variables

```properties
stripe.api-key=${STRIPE_SECRET_KEY}
stripe.webhook-secret=${STRIPE_WEBHOOK_SECRET}
stripe.public-key=${STRIPE_PUBLIC_KEY}
```

### Maven Dependency

```xml
<dependency>
  <groupId>com.stripe</groupId>
  <artifactId>stripe-java</artifactId>
  <version>22.5.0</version>
</dependency>
```

## Security Considerations

1. **API Key**: Never expose in client-side code
2. **Webhook Verification**: Always verify signature
3. **PCI DSS**: Never store card data (handled by Stripe)
4. **HTTPS**: All communication over TLS 1.2+
5. **Amount Validation**: Verify amount on backend before charging

## Monitoring

### Metrics

- Payment success rate
- Average payment time
- Failed payments by reason
- Dispute rate
- Payout processing time

### Dashboard

Access Stripe Dashboard for:
- Real-time payment tracking
- Dispute management
- Seller payouts
- Transaction history

## References

- [Stripe Documentation](https://stripe.com/docs)
- [Stripe Node.js SDK](https://github.com/stripe/stripe-node)
- [PCI Compliance](https://en.wikipedia.org/wiki/Payment_Card_Industry_Data_Security_Standard)

---

**Last Updated**: 2026-05-05
