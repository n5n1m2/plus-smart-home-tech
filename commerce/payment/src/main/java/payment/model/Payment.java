package payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private UUID paymentId;

    private UUID orderId;

    private Double productCost;
    private Double deliveryCost;
    private Double totalCost;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
