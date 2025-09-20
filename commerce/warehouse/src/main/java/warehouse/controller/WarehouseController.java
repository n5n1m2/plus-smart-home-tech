package warehouse.controller;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseFeignClient {
    private final WarehouseService service;

    @Override
    @PutMapping
    public void registerNewProduct(@RequestBody NewProductRequest request) {
        service.addNewProduct(request);
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        service.addQuantity(request);
    }

    @Override
    public BookedProductDto checkAvailability(CartDto cart) {
        return service.bookProduct(cart);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return service.getCurrentAddress();
    }
}
