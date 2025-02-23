package com.universityweb.ecommerceservice.payment.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.payment.entity.Payment;
import com.universityweb.payment.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper extends BaseMapper<Payment, PaymentResponse> {
    @Mapping(source = "order.id", target = "orderId")
    @Override
    PaymentResponse toDTO(Payment entity);

    @Mapping(target = "order", ignore = true)
    @Override
    Payment toEntity(PaymentResponse dto);
}
