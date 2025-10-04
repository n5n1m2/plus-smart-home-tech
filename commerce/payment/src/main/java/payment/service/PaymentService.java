package payment.service;

import interaction.client.OrderFeignClient;
import interaction.client.StoreFeignClient;
import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment.model.Payment;
import payment.model.PaymentStatus;
import payment.model.mapper.PaymentMapper;
import payment.repository.PaymentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderFeignClient orderClient;
    private final StoreFeignClient storeClient;

    public Double calculateProductCost(OrderDto order) {
        return order.getProducts().entrySet().stream()
                .map(entry -> {
                    UUID productId = entry.getKey();
                    int quantity = entry.getValue();
                    double price = storeClient.getProduct(productId).getPrice(); // getPrice() → double
                    return price * quantity;
                })
                .reduce(0.0, Double::sum);
    }

    public Double calculateTotalCost(OrderDto order) {
        Double productCost = calculateProductCost(order);
        Double deliveryCost = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : 50.0;
        Double vat = productCost * 0.10;
        return productCost + deliveryCost + vat;
    }

    public PaymentDto createPayment(OrderDto order) {
        Double productCost = calculateProductCost(order);
        Double deliveryCost = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : 50.0;
        Double vat = productCost * 0.10;
        Double total = productCost + deliveryCost + vat;

        Payment payment = new Payment(
                UUID.randomUUID(),
                order.getOrderId(),
                productCost,
                deliveryCost,
                total,
                PaymentStatus.PENDING
        );
        return mapper.toDto(repository.save(payment));
    }

    public void markSuccess(UUID paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment with id " + paymentId + " not found"));
        payment.setStatus(PaymentStatus.SUCCESS);
        orderClient.payment(payment.getOrderId());
        repository.save(payment);
    }

    public void markFailed(UUID paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment with id " + paymentId + " not found"));
        payment.setStatus(PaymentStatus.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        repository.save(payment);
    }

}
