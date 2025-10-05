package interaction.model.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private UUID deliveryId;
    private UUID orderId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private double weight;
    private double volume;
    private boolean fragile;
    private String deliveryStatus;
}
