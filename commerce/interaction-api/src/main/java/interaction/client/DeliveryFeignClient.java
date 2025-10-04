package interaction.client;

import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto dto);

    @PostMapping("/cost")
    Double deliveryCost(@RequestBody OrderDto dto);

    @PostMapping("/picked")
    DeliveryDto markPicked(@RequestBody UUID orderId);

    @PostMapping("/successful")
    DeliveryDto markDelivered(@RequestBody UUID orderId);

    @PostMapping("/failed")
    DeliveryDto markFailed(@RequestBody UUID orderId);
}
