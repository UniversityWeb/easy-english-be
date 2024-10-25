package com.universityweb.payment.mapper;

import com.universityweb.payment.entity.Payment;
import com.universityweb.payment.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toDTO(Payment entity);

    List<PaymentResponse> toDTOs(List<Payment> entities);

    @Mapping(target = "order", ignore = true)
    Payment toEntity(PaymentResponse dto);

    List<Payment> toEntities(List<PaymentResponse> dtos);
}
