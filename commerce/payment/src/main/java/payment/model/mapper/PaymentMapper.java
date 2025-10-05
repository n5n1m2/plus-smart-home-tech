package payment.model.mapper;

import interaction.model.payment.PaymentDto;
import org.mapstruct.Mapper;
import payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);
    Payment fromDto(PaymentDto paymentDto);
}
