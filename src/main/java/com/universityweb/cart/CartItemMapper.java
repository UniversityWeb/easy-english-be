package com.universityweb.cart;

import com.universityweb.cart.model.CartItem;
import com.universityweb.cart.model.CartItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "course.id", target = "courseId")
    CartItemDTO toDTO(CartItem cartItem);

    List<CartItemDTO> toDTOs(List<CartItem> cartItems);

    CartItem toEntity(CartItemDTO cartItemDTO);

    List<CartItem> toEntities(List<CartItemDTO> cartItemDTOs);
}
