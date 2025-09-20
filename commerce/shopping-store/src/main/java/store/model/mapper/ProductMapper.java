package store.model.mapper;

import interaction.model.store.ProductDto;
import org.mapstruct.Mapper;
import store.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto productToProductDto(Product product);

    Product productDtoToProduct(ProductDto productDto);
}
