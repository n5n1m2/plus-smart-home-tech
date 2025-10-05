package delivery.controller;

import delivery.service.DeliveryService;
import interaction.client.DeliveryFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeignClient {
    private final DeliveryService service;

    @Override
    @PutMapping
    public DeliveryDto planDelivery(@RequestBody DeliveryDto dto) {
        return service.createDelivery(dto);
    }

    @Override
    @PostMapping("/cost")
    public Double deliveryCost(@RequestBody OrderDto dto) {
        return service.calculateDeliveryPrice(dto);
    }

    @Override
    @PostMapping("/picked")
    public DeliveryDto markPicked(@RequestBody UUID orderId) {
        return service.markPickedDelivery(orderId);
    }

    @Override
    @PostMapping("/successful")
    public DeliveryDto markDelivered(@RequestBody UUID orderId) {
        return service.markDelivered(orderId);
    }

    @Override
    @PostMapping("/failed")
    public DeliveryDto markFailed(@RequestBody UUID orderId) {
        return service.markFailedDelivery(orderId);
    }
}
