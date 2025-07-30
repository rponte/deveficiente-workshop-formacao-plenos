@Component
public class TransactionalPaymentListener {

    @Transactional
    @RabbitListener(
        queues = "payment.queue",
        ackMode = "TRANSACTIONAL" // (⚠️) Exaclty Once Delivery via DISTRIBUTED TRANSACTION
    )
    public void handlePaymentMessage(Order order) {
        System.out.println("Received payment notification: " + order);

        // Simulate business logic
        processPayment(order);

        // If an exception is thrown here, the message will be requeued
    }

    private void processPayment(Order order) {
        System.out.println("Processing payment for Order ID: " + order.getId());
    }
}
