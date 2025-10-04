package order.model;

import interaction.model.order.OrderState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private UUID orderId;

    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    private UUID shoppingCartId;
    private UUID deliveryId;
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private double deliveryWeight;
    private double deliveryVolume;
    private boolean fragile;

    private Double productPrice;
    private Double deliveryPrice;
    private Double totalPrice;
}
