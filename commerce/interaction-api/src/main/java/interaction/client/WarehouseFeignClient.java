package interaction.client;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductRequest;
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
}
