package interaction.model.order;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Setter
@Getter
public class OrderDto {

    private UUID orderId;

    private Map<UUID, Integer> products;

    private UUID shoppingCartId;
    private UUID deliveryId;
    private UUID paymentId;
    private OrderState state;
    private double deliveryWeight;
    private double deliveryVolume;
    private boolean fragile;

    private Double productPrice;
    private Double deliveryPrice;
    private Double totalPrice;
}
