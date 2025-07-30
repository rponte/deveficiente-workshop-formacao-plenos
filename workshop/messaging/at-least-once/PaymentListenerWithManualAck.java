@Component
public class PaymentListener {

    @RabbitListener(
        queues = "payment.queue",
        ackMode = "MANUAL"
    )
    public void handlePaymentMessage(Order order, Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            System.out.println("Received payment message: " + order);
            processPayment(order);

            // Send ACK manually after successful processing
            channel.basicAck(deliveryTag, false); // multiple = false
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());

            // Optional: Reject and requeue (true) or discard (false)
            channel.basicNack(deliveryTag, false, true); // requeue = true
        }
    }

    private void processPayment(Order order) {
        System.out.println("Processing payment for Order ID: " + order.getId());
    }
}
