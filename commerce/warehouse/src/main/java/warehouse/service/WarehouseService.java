package warehouse.service;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductRequest;

public interface WarehouseService {
    void addNewProduct(NewProductRequest request);

    void addQuantity(AddProductToWarehouseRequest request);

    BookedProductDto bookProduct(CartDto cart);

    AddressDto getCurrentAddress();
}
