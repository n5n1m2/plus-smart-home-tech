package store.model;

import interaction.model.cart.ProductCategory;
import interaction.model.cart.ProductState;
import interaction.model.store.QuantityState;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private UUID productId;

    @Column
    private String productName;

    @Column
    private String description;

    @Column
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column
    private ProductCategory productCategory;

    @Column
    private Double price;
}
