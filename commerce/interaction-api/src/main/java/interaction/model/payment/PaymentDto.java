package interaction.model.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal productCost;
    private BigDecimal deliveryCost;
    private BigDecimal totalCost;
    private String status;
}
