@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final OrderRepository orderRepository;
    private final PaymentNotifier paymentNotifier;

    public CheckoutController(OrderRepository orderRepository, PaymentNotifier paymentNotifier) {
        this.orderRepository = orderRepository;
        this.paymentNotifier = paymentNotifier;
    }

    @PostMapping
    public ResponseEntity<String> checkout(@RequestBody Order order) {
        // Save order in the database
        Order savedOrder = orderRepository.save(order);
        // Notifiy payment service via RabbitMQ
        paymentNotifier.notifyPayment(savedOrder);
        // Return um HTTP 200-OK
        return ResponseEntity
                    .ok("Checkout completed and payment message sent to RabbitMQ.");
    }
}


@Component
public class PaymentNotifier {

    private final RabbitTemplate rabbitTemplate;

    public PaymentNotifier(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Retryable(
        retryFor = { AnyRabbitMQException.class },
        maxAttempts = 5,
        backoff = @Retryable.Backoff(delay = 2000, multiplier = 2)
    )
    public void notifyPayment(Order order) {
        rabbitTemplate.convertAndSend("payment.exchange", "payment.notify", order);
    }
}
