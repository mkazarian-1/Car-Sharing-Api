package org.example.carsharingapi.mapper;

import org.example.carsharingapi.config.MapperConfig;
import org.example.carsharingapi.dto.payment.PaymentDto;
import org.example.carsharingapi.dto.payment.PaymentResponseDto;
import org.example.carsharingapi.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = RentalMapper.class)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);

    PaymentResponseDto toResponseDto(Payment payment);
}
