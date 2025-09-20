package store.service;

import interaction.model.cart.ProductCategory;
import interaction.model.store.ProductDto;
import interaction.model.store.QuantityState;
import interaction.model.store.SetProductQuantityStateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StoreService {
    Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID id);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean deleteProductById(UUID id);

    boolean updateQuantity(UUID id, QuantityState state);
}
