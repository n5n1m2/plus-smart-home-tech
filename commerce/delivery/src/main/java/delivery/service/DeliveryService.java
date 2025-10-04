package delivery.service;

import delivery.model.Delivery;
import delivery.model.DeliveryStatus;
import delivery.model.mapper.DeliveryMapper;
import delivery.repository.DeliveryRepository;
import interaction.client.OrderFeignClient;
import interaction.client.WarehouseFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import interaction.model.warehouse.ShipmentRequest;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository repository;
    private final DeliveryMapper deliveryMapper;
    private final OrderFeignClient orderClient;
    private final WarehouseFeignClient warehouseClient;

    public DeliveryDto createDelivery(DeliveryDto dto) {

        Delivery delivery = deliveryMapper.fromDeliveryDto(dto);

        delivery.setDeliveryStatus(DeliveryStatus.CREATED);

        return deliveryMapper.toDeliveryDto(repository.save(delivery));
    }

    public double calculateDeliveryPrice(OrderDto dto) {
        Delivery delivery = repository.findById(dto.getDeliveryId())
                .orElseThrow(() -> new NotFoundException("Delivery for order with id " + dto.getDeliveryId() + " not found"));

        double cost = 5.0;

        if (delivery.getFromAddress().getStreet().contains("ADDRESS_2")) {
            cost = cost * 2 + 5;
        } else if (delivery.getFromAddress().getStreet().contains("ADDRESS_1")) {
            cost = cost * 1 + 5;
        }

        if (delivery.isFragile()) {
            cost += cost * 0.2;
        }

        cost += delivery.getWeight() * 0.3;
        cost += delivery.getVolume() * 0.2;

        if (!delivery.getFromAddress().getStreet().equalsIgnoreCase(delivery.getToAddress().getStreet())) {
            cost += cost * 0.2;
        }
        return BigDecimal.valueOf(cost)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public DeliveryDto markPickedDelivery(UUID orderId) {
        Delivery delivery = repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("delivery for order id " + orderId + " not found"));

        delivery.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
        orderClient.delivery(orderId);
        warehouseClient.shippedToDelivery(new ShipmentRequest(orderId, delivery.getDeliveryId()));

        return deliveryMapper.toDeliveryDto(repository.save(delivery));
    }

    public DeliveryDto markDelivered(UUID orderId) {
        Delivery delivery = repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("delivery for order id " + orderId + " not found"));

        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
        orderClient.delivery(orderId);

        return deliveryMapper.toDeliveryDto(repository.save(delivery));
    }

    public DeliveryDto markFailedDelivery(UUID orderId) {
        Delivery delivery = repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("delivery for order id " + orderId + " not found"));

        delivery.setDeliveryStatus(DeliveryStatus.FAILED);
        orderClient.deliveryFailed(orderId);

        return deliveryMapper.toDeliveryDto(repository.save(delivery));
    }

}
