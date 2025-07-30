@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final OrderRepository orderRepository;
    private final PaymentNotificationClient paymentClient;

    public CheckoutController(OrderRepository orderRepository, PaymentNotificationClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

    @PostMapping
    public ResponseEntity<String> checkout(@RequestBody Order order) {
        // Save order in the database
        Order savedOrder = orderRepository.save(order);
        // Notifiy payment service via HTTP
        paymentClient.notifyPayment(savedOrder);
        // Return um HTTP 200-OK
        return ResponseEntity
                .ok("Checkout completed and payment notified.");
    }
}


@Component
public class PaymentNotificationClient {

    private final RestTemplate restTemplate;

    public PaymentNotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void notifyPayment(Order order) {
        PaymentRequest payload = new PaymentRequest(order);
        String paymentServiceUrl = "https://api.payment-provider.com/payments"; // Replace with actual URL

        restTemplate.postForEntity(paymentServiceUrl, payload, Void.class);
    }

    private static class PaymentRequest {
        private String email;
        private String orderId;
        private String amount;

        public PaymentRequest(Order order) {
            this.email = order.getCustomerEmail();
            this.orderId = order.getId().toString();
            this.amount = order.getTotalAmount().toString();
        }

        // Getters and setters (needed by Jackson)
    }
}
