package interaction.client;

import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

    @PostMapping("/productCost")
    Double getProductCost(@RequestBody OrderDto order);

    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody OrderDto order);

    @PostMapping
    PaymentDto createPayment(@RequestBody OrderDto order);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
