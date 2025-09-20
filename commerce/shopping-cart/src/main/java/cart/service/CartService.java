package cart.service;

import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {
    CartDto getCart(String name);

    @Transactional
    CartDto addProductToCart(String name, Map<UUID, Long> products);

    @Transactional
    CartDto removeProductFromCart(String name, List<UUID> products);

    @Transactional
    CartDto changeProductQuantity(String name, ChangeProductQuantityRequest request);

    @Transactional
    void deactivateCart(String name);
}
