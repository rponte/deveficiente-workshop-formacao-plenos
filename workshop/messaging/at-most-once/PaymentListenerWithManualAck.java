@Component
public class PaymentListener {

    @RabbitListener(
        queues = "payment.queue",
        ackMode = "MANUAL"
    )
    public void handlePaymentMessage(Order order, Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        // Send ACK manually after successful processing
        channel.basicAck(deliveryTag, false); // multiple = false
       
        System.out.println("Received payment message: " + order);
        processPayment(order);
    }

    private void processPayment(Order order) {
        System.out.println("Processing payment for Order ID: " + order.getId());
    }
}
