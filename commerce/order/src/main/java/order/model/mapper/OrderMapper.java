package order.model.mapper;

import interaction.model.order.OrderDto;
import order.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto dtoFromOrder(Order order);

    Order orderFromDto(OrderDto orderDto);
}
