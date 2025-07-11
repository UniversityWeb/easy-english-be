package com.universityweb.cart.response;

import com.universityweb.cart.entity.CartItem;
import com.universityweb.course.response.CourseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse implements Serializable {
    private Long id;

    @Schema(description = "Status of the cart item",
            example = "ACTIVE")
    private CartItem.EStatus status;

    @Schema(description = "Price of the item",
            example = "99.99")
    private BigDecimal price;

    @Schema(description = "Date and time when the item was last updated",
            example = "2024-09-28T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Details of the course associated with the cart item")
    private CourseResponse course;

    @Schema(description = "Cart ID to which this item belongs",
            example = "1")
    private Long cartId;

    private Long bundleId;
}
