package com.universityweb.order.dto;

import com.universityweb.course.response.CourseResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDTO implements Serializable {
    Long id;
    BigDecimal price;
    BigDecimal discountPercent;
    CourseResponse course;
    Long orderId;
}
