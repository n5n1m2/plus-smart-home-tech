package warehouse.service;

import interaction.model.delivery.AddressDto;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.*;

public interface WarehouseService {
    void addNewProduct(NewProductRequest request);

    void addQuantity(AddProductToWarehouseRequest request);

    BookedProductDto bookProduct(CartDto cart);

    AddressDto getCurrentAddress();

    BookedProductDto assembleProducts(AssemblyRequest request);

    void markAsShipped(ShipmentRequest request);

    void returnProducts(ReturnRequest request);
}
