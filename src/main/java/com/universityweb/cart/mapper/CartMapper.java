package com.universityweb.cart.mapper;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.response.CartResponse;
import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper extends BaseMapper<Cart, CartResponse> {
    @Mapping(source = "user.username", target = "username")
    @Override
    CartResponse toDTO(Cart entity);

    @Override
    Cart toEntity(CartResponse dto);
}
