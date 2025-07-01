package workshop.arquitetura.payments;

@RestController
@RequestMapping("/api/payments")
public class CancelPaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    private final WebClient webClient = WebClient.create("https://fake-gateway.com/api");

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> cancelPayment(@PathVariable Long paymentId) {
        
        Optional<Payment> optional = paymentRepository.findById(paymentId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Payment payment = optional.get();
        if (payment.getStatus() == PaymentStatus.CANCELADO) {
            return ResponseEntity.badRequest().body("Payment is already canceled.");
        }

        // 1. Atualiza localmente
        payment.setStatus(PaymentStatus.CANCELADO);
        payment.setCanceledAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 2. Cancela no gateway externo
        try {
            webClient.post()
                    .uri("/cancel")
                    .bodyValue(Map.of(
                        "paymentId", payment.getExternalId(),
                        "reason", "Solicitado pelo cliente"
                    ))
                    .retrieve()
                    .toBodilessEntity()
                    .block(); // chamada síncrona

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Pagamento cancelado localmente, mas falhou no gateway externo.");
        }

        return ResponseEntity.ok("Pagamento cancelado com sucesso.");
    }
}

