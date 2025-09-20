package cart.controller;

import cart.service.CartService;
import interaction.client.CartFeignClient;
import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-cart")
@RestController
@RequiredArgsConstructor
public class CartController implements CartFeignClient {
    private final CartService service;

    @Override
    @GetMapping
    public CartDto getCart(@Valid @NotEmpty @RequestParam String username) {
        return service.getCart(username);
    }

    @Override
    @PutMapping
    public CartDto addProduct(@Valid @NotEmpty String username, @RequestBody Map<UUID, Long> productsToAdd) {
        return service.addProductToCart(username, productsToAdd);
    }

    @Override
    @PostMapping("/remove")
    public CartDto removeProducts(@Valid @NotEmpty String username, @RequestBody List<UUID> productIds) {
        return service.removeProductFromCart(username, productIds);
    }

    @Override
    @PostMapping("/change-quantity")
    public CartDto changeQuantity(@Valid @NotEmpty @RequestParam String username, @RequestBody ChangeProductQuantityRequest request) {
        return service.changeProductQuantity(username, request);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@Valid @NotEmpty @RequestParam String username) {
        service.deactivateCart(username);
    }
}
