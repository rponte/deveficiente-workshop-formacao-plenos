@Component
public class PaymentListener {

    @RabbitListener(
        queues = "payment.queue",
        ackMode = "AUTO" // AT-LEAST ONCE (default)
    )
    public void handlePaymentMessage(Order order) {
        System.out.println("Received payment notification: " + order);

        // Simulate business logic
        processPayment(order);

        // If an exception is thrown here, the message will be requeued
    }

    private void processPayment(Order order) {
        System.out.println("Processing payment for Order ID: " + order.getId());

        // Uncomment to simulate failure and trigger redelivery
        // if (true) throw new RuntimeException("Simulated failure");
    }
}
