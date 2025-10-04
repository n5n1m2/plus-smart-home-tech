package interaction.client;

import interaction.model.delivery.AddressDto;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallbackFactory = WarehouseClientFallbackFactory.class)
public interface WarehouseFeignClient {

    @PutMapping
    void registerNewProduct(@RequestBody NewProductRequest request);

    @PostMapping("/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/check")
    BookedProductDto checkAvailability(@RequestBody CartDto cart);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/assembly")
    BookedProductDto assemblyProductForOrderFromShoppingCart(@RequestBody AssemblyRequest request);

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShipmentRequest request);

    @PostMapping("/return")
    void returnProducts(@RequestBody ReturnRequest request);
}
