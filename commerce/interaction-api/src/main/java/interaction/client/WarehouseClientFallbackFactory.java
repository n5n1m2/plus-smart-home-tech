package interaction.client;

import interaction.model.delivery.AddressDto;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.*;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WarehouseClientFallbackFactory implements FallbackFactory<WarehouseFeignClient> {
    @Override
    public WarehouseFeignClient create(Throwable cause) {
        return new WarehouseFeignClient() {

            @Override
            public void registerNewProduct(NewProductRequest request) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }

            @Override
            public void addProductQuantity(AddProductToWarehouseRequest request) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }

            @Override
            public BookedProductDto checkAvailability(CartDto cart) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }

            @Override
            public AddressDto getWarehouseAddress() {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }

            @Override
            public BookedProductDto assemblyProductForOrderFromShoppingCart(AssemblyRequest request) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }

            @Override
            public void shippedToDelivery(ShipmentRequest request) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }

            @Override
            public void returnProducts(ReturnRequest request) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
            }
        };
    }
}
