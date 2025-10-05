package order.service;

import interaction.client.DeliveryFeignClient;
import interaction.client.PaymentFeignClient;
import interaction.client.WarehouseFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import interaction.model.order.OrderState;
import interaction.model.payment.PaymentDto;
import interaction.model.warehouse.AssemblyRequest;
import interaction.model.warehouse.BookedProductDto;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import order.model.Order;
import order.model.mapper.OrderMapper;
import order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper mapper;
    private final OrderRepository repository;
    private final WarehouseFeignClient warehouseClient;
    private final PaymentFeignClient paymentClient;
    private final DeliveryFeignClient deliveryClient;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setState(OrderState.NEW);
        Order order = mapper.orderFromDto(orderDto);
        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public List<OrderDto> getOrders(UUID cartId) {
        return repository.findAllByShoppingCartId(cartId).stream()
                .map(mapper::dtoFromOrder)
                .toList();
    }

    @Override
    public OrderDto getOrder(UUID orderId) {
        return mapper.dtoFromOrder(repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found")));
    }

    @Override
    public OrderDto assembleOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
        BookedProductDto bookedProductDto = warehouseClient.assemblyProductForOrderFromShoppingCart(
                new AssemblyRequest(orderId, order.getProducts())
        );

        order.setState(OrderState.ASSEMBLED);
        order.setState(OrderState.ASSEMBLED);
        order.setDeliveryWeight(bookedProductDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductDto.getDeliveryVolume());
        order.setFragile(bookedProductDto.isFragile());

        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markAssemblyFailed(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markAsPaid(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
        PaymentDto paymentDto = paymentClient.createPayment(mapper.dtoFromOrder(order));
        order.setState(OrderState.PAID);
        order.setPaymentId(paymentDto.getPaymentId());
        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markPaymentFailed(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
        paymentClient.paymentFailed(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markAsDelivered(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
        DeliveryDto deliveryDto = deliveryClient.markDelivered(orderId);
        order.setState(OrderState.DELIVERED);
        order.setDeliveryId(deliveryDto.getDeliveryId());

        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markAsDeliveredFailed(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        deliveryClient.markFailed(orderId);
        order.setState(OrderState.DELIVERY_FAILED);

        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markAsCompeted(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        order.setState(OrderState.COMPLETED);

        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto markAsReturned(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        order.setState(OrderState.PRODUCT_RETURNED);

        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        Double deliveryPrice = deliveryClient.deliveryCost(mapper.dtoFromOrder(order));
        order.setDeliveryPrice(deliveryPrice);

        return mapper.dtoFromOrder(repository.save(order));
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));

        Double productPrice = paymentClient.getProductCost(mapper.dtoFromOrder(order));
        Double total = paymentClient.getTotalCost(mapper.dtoFromOrder(order));
        order.setProductPrice(productPrice);
        order.setTotalPrice(total);

        return mapper.dtoFromOrder(repository.save(order));
    }
}
