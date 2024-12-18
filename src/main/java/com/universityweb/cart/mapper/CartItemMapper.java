package com.universityweb.cart.mapper;

import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper extends BaseMapper<CartItem, CartItemResponse> {
    @Mapping(source = "cart.id", target = "cartId")
    @Override
    CartItemResponse toDTO(CartItem cartItem);

    @Override
    CartItem toEntity(CartItemResponse cartItemDTO);
}
