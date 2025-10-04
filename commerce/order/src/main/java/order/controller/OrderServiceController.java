package order.controller;

import interaction.client.OrderFeignClient;
import interaction.model.order.OrderDto;
import lombok.RequiredArgsConstructor;
import order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderServiceController implements OrderFeignClient {
    private final OrderService service;

    @Override
    @PutMapping
    public OrderDto create(@RequestBody OrderDto dto){
        return service.createOrder(dto);
    }

    @Override
    @GetMapping
    public List<OrderDto> byCart(@RequestParam UUID cartId){
        return service.getOrders(cartId);
    }

    @Override
    @GetMapping("/{id}")
    public OrderDto byId(@PathVariable UUID id){
        return service.getOrder(id);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody UUID orderId){
        return service.assembleOrder(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody UUID orderId){
        return service.markAssemblyFailed(orderId);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payment(@RequestBody UUID orderId){
        return service.markAsPaid(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId){
        return service.markPaymentFailed(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody UUID orderId){
        return service.markAsDelivered(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId){
        return service.markAsDeliveredFailed(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto complete(@RequestBody UUID orderId){
        return service.markAsCompeted(orderId);
    }

    @Override
    @PostMapping("/return")
    public OrderDto productReturn(@RequestBody UUID orderId){
        return service.markAsReturned(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(@RequestBody UUID orderId){
        return service.calculateDeliveryPrice(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(@RequestBody UUID orderId){
        return service.calculateTotalPrice(orderId);
    }
}
