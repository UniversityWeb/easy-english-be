package com.universityweb.price.entity;

import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "prices")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate  endDate;

    @Column(name = "active")
    private Boolean isActive;

    @OneToOne(mappedBy = "price")
    private Course course;

    /**
     * Utility method to get the price, considering the sale price validity.
     * If the current date is within the sale period, it returns the sale price.
     * Otherwise, it returns the regular price.
     *
     * @return the applicable price (sale price or regular price).
     */
    public BigDecimal getApplicablePrice() {
        // Check if sale price exists and is within the valid date range
        if (salePrice != null &&
                LocalDate.now().isAfter(startDate) &&
                LocalDate.now().isBefore(endDate)) {
            return salePrice;  // Return sale price if within valid date range
        }
        return price;  // Otherwise, return the regular price
    }
}
