package com.universityweb.order.dto;

import com.universityweb.course.response.CourseResponse;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO implements Serializable {
    private Long id;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private CourseResponse course;
    private Long orderId;
}
