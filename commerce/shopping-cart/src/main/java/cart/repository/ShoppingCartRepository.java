package cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cart.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUsername(String username);
}
