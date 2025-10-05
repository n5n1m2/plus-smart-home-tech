package order.service;

import interaction.model.order.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);

    List<OrderDto> getOrders(UUID cartId);

    OrderDto getOrder(UUID orderId);

    OrderDto assembleOrder(UUID orderId);

    OrderDto markAssemblyFailed(UUID orderId);

    OrderDto markAsPaid(UUID orderId);

    OrderDto markPaymentFailed(UUID orderId);

    OrderDto markAsDelivered(UUID orderId);

    OrderDto markAsDeliveredFailed(UUID orderId);

    OrderDto markAsCompeted(UUID orderId);

    OrderDto markAsReturned(UUID orderId);

    OrderDto calculateDeliveryPrice(UUID orderId);

    OrderDto calculateTotalPrice(UUID orderId);
}
