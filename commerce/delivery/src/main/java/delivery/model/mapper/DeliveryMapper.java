package delivery.model.mapper;

import delivery.model.Address;
import delivery.model.Delivery;
import interaction.model.delivery.AddressDto;
import interaction.model.delivery.DeliveryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    DeliveryDto toDeliveryDto(Delivery delivery);

    Delivery fromDeliveryDto(DeliveryDto deliveryDto);

    AddressDto toAddressDto(Address address);

    Address fromAddressDto(AddressDto addressDto);
}
