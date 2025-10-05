package payment;

import interaction.client.PaymentFeignClient;
import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payment.service.PaymentService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeignClient {
    private final PaymentService service;

    @Override
    @PostMapping("/productCost")
    public Double getProductCost(@RequestBody OrderDto order) {
        return service.calculateProductCost(order);
    }

    @Override
    @PostMapping("/totalCost")
    public Double getTotalCost(@RequestBody OrderDto order) {
        return service.calculateTotalCost(order);
    }

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto order) {
        return service.createPayment(order);
    }

    @Override
    @PostMapping("/refund")
    public void paymentSuccess(@RequestBody UUID paymentId) {
        service.markSuccess(paymentId);
    }

    @Override
    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        service.markFailed(paymentId);
    }
}
