package warehouse.service;

import interaction.model.delivery.AddressDto;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse.model.WarehouseMapper;
import warehouse.model.WarehouseProduct;
import warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESS = {"ADDRESS_1", "ADDRESS_2"};
    private final static String currentAddress = ADDRESS[new SecureRandom().nextInt(ADDRESS.length)];
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;
    private final Map<UUID, OrderBooking> bookings = new HashMap<>();

    @Override
    public void addNewProduct(NewProductRequest request) {
        if (request.getProductId() != null && repository.existsById(request.getProductId())) {
            throw new IllegalArgumentException("Product already exists");
        }

        WarehouseProduct product = new WarehouseProduct();
        product.setProductId(request.getProductId());
        product.setFragile(request.isFragile());
        product.setDimension(mapper.dtoToDimension(request.getDimension()));
        product.setWeight(request.getWeight());
        product.setQuantity(0L);
        repository.save(product);
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + request.getProductId() + " not found"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
    }

    @Override
    public BookedProductDto bookProduct(CartDto cart) {
        Map<UUID, Long> productQuantityToBooking = cart.getProducts();
        List<WarehouseProduct> products = repository.findAllByProductIdIn(productQuantityToBooking.keySet());

        if (products.size() != productQuantityToBooking.size()) {

            List<UUID> notFoundedProducts = new ArrayList<>();
            List<UUID> foundedProducts = products.stream()
                    .map(WarehouseProduct::getProductId)
                    .toList();

            for (UUID uuid : productQuantityToBooking.keySet()) {
                if (!foundedProducts.contains(uuid)) {
                    notFoundedProducts.add(uuid);
                }
            }

            throw new IllegalArgumentException("Not founded products: \n " + notFoundedProducts);
        }

        Map<UUID, Long> productBalanceAfterBooking = new HashMap<>();

        double totalVolume = 0;
        double totalWeight = 0;
        boolean fragile = false;

        for (WarehouseProduct product : products) {
            productBalanceAfterBooking.put(product.getProductId(),
                    product.getQuantity() - productQuantityToBooking.get(product.getProductId()));

            if (product.isFragile()) {
                fragile = true;
            }
            totalWeight += product.getWeight();
            totalVolume += product.getWeight();
        }

        List<UUID> notEnoughProducts = productBalanceAfterBooking.entrySet().stream()
                .filter((entry) -> entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .toList();

        if (!notEnoughProducts.isEmpty()) {
            throw new IllegalArgumentException("Not enough products: \n " + notEnoughProducts);
        }
        return new BookedProductDto(totalWeight, totalVolume, fragile);
    }

    @Override
    public AddressDto getCurrentAddress() {
        return new AddressDto(currentAddress, currentAddress, currentAddress, currentAddress, currentAddress);
    }

    @Override
    public BookedProductDto assembleProducts(AssemblyRequest request) {
        Map<UUID, Long> productQuantityToAssemble = request.getProducts().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue()));
        CartDto cart = new CartDto();
        cart.setProducts(productQuantityToAssemble);

        BookedProductDto products = bookProduct(cart);
        bookings.put(request.getOrderId(),
                new OrderBooking(request.getOrderId(), null, productQuantityToAssemble));
        return products;
    }

    @Override
    public void markAsShipped(ShipmentRequest request) {
        OrderBooking orderBooking = bookings.get(request.getOrderId());
        if (orderBooking == null) {
            throw new IllegalArgumentException("booking not found for order with id " + request.getOrderId());
        }
        orderBooking.setDeliveryId(request.getDeliveryId());
    }

    @Override
    public void returnProducts(ReturnRequest request) {
        List<UUID> productIds = request.getProducts().keySet().stream().toList();
        List<WarehouseProduct> products = repository.findAllByProductIdIn(productIds);

        if (products.size() != productIds.size()) {
            List<UUID> foundedProducts = products.stream()
                    .map(WarehouseProduct::getProductId)
                    .toList();
            List<UUID> notFoundedProducts = new ArrayList<>();
            for (UUID uuid : productIds) {
                if (!foundedProducts.contains(uuid)) {
                    notFoundedProducts.add(uuid);
                }
            }
            throw new IllegalArgumentException("Not founded products: \n " + notFoundedProducts);
        }

        for (WarehouseProduct product : products) {
            product.setQuantity(product.getQuantity() + request.getProducts().get(product.getProductId()));
        }

        repository.saveAll(products);
    }
}
