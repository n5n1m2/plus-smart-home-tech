package cart.service;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cart.model.Cart;
import cart.model.ShoppingCartState;
import cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final ShoppingCartRepository repository;
    private final WarehouseFeignClient warehouseClient;

    @Override
    public CartDto getCart(String name) {
        Cart cart = findExistingCart(name);
        return new CartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    @CircuitBreaker(name = "warehouse", fallbackMethod = "addProductFailBack")
    public CartDto addProductToCart(String name, Map<UUID, Long> products) {
        Cart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart has been deactivated");
        }

        Map<UUID, Long> productsMap = cart.getProducts();
        products.forEach((id, quantity) -> productsMap.merge(id, quantity, Long::sum));

        warehouseClient.checkAvailability(new CartDto(cart.getShoppingCartId(), products));

        repository.save(cart);
        return new CartDto(cart.getShoppingCartId(), productsMap);
    }

    @Transactional
    @Override
    public CartDto removeProductFromCart(String name, List<UUID> products) {
        Cart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart has been deactivated");
        }
        products.forEach(cart.getProducts()::remove);
        repository.save(cart);
        return new CartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public CartDto changeProductQuantity(String name, ChangeProductQuantityRequest request) {
        Cart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart has been deactivated");
        }
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new IllegalArgumentException("Product not found");
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        repository.save(cart);
        return new CartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public void deactivateCart(String name) {
        Cart cart = findExistingCart(name);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            return;
        }
        cart.setState(ShoppingCartState.DEACTIVATED);
        repository.save(cart);
    }

    public CartDto addProductFailBack(String name, Map<UUID, Long> products, Throwable ex) {
        Cart cart = repository.findByUsername(name).orElseGet(() -> null);
        if (cart != null) {
            return new CartDto(cart.getShoppingCartId(), cart.getProducts());
        }
        return new CartDto(null, null);
    }

    private Cart findExistingCart(String username) {
        return repository.findByUsername(username).orElseGet(
                () -> repository.save(createNewCart(username))
        );
    }

    private Cart createNewCart(String username) {
        Cart cart = new Cart();
        cart.setUsername(username);
        cart.setShoppingCartId(UUID.randomUUID());
        cart.setProducts(new HashMap<>());
        cart.setState(ShoppingCartState.ACTIVE);
        return cart;
    }
}
