@Component
public class PaymentListener {

    @RabbitListener(
        queues = "payment.queue",
        ackMode = "NONE" // <- AT-MOST ONCE!
    )
    public void handlePaymentMessage(Order order) {
        System.out.println("Received payment notification: " + order);

        // Simulate processing logic
        // Any exception here will NOT cause redelivery
        processPayment(order);
    }

    private void processPayment(Order order) {
        // Your payment logic here (e.g. call payment gateway)
        System.out.println("Processing payment for Order ID: " + order.getId());
    }
}
