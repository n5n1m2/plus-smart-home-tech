package warehouse.controller;

import interaction.client.WarehouseFeignClient;
import interaction.model.delivery.AddressDto;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @Override
    @PostMapping("/assembly")
    public BookedProductDto assemblyProductForOrderFromShoppingCart(@RequestBody AssemblyRequest request) {
        return service.assembleProducts(request);
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(@RequestBody ShipmentRequest request) {
        service.markAsShipped(request);
    }

    @Override
    @PostMapping("/return")
    public void returnProducts(@RequestBody ReturnRequest request) {
        service.returnProducts(request);
    }
}
