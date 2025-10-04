package interaction.model.warehouse;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReturnRequest {
    private UUID orderId;
    private Map<UUID, Integer> products;
}
