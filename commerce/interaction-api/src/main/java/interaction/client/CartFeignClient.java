package interaction.client;

import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartFeignClient {
    @GetMapping
    CartDto getCart(@Valid @NotEmpty @RequestParam String username);

    @PutMapping
    CartDto addProduct(@Valid @NotEmpty @RequestParam String username, @RequestBody Map<UUID, Long> productsToAdd);

    @PostMapping("/remove")
    CartDto removeProducts(@Valid @NotEmpty @RequestParam String username, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    CartDto changeQuantity(@Valid @NotEmpty @RequestParam String username, @RequestBody ChangeProductQuantityRequest request);

    @DeleteMapping
    void deactivateCart(@Valid @NotEmpty @RequestParam String username);
}
