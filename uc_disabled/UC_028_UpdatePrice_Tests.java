package com.universityweb.common.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.price.PriceRepository;
import com.universityweb.price.entity.Price;
import com.universityweb.price.request.PriceRequest;
import com.universityweb.price.response.PriceResponse;
import com.universityweb.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_028_UpdatePrice_Tests {

    @InjectMocks
    private PriceService priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePrice_Success() {
        // Arrange
        PriceRequest priceRequest = new PriceRequest(1L, null, new BigDecimal("100.00"), new BigDecimal("80.00"), LocalDate.now(), LocalDate.now().plusDays(10), true);
        Price existingPrice = Price.builder()
                .id(1L)
                .price(new BigDecimal("90.00"))
                .salePrice(new BigDecimal("70.00"))
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(5))
                .isActive(true)
                .build();

        when(priceRepository.findById(1L)).thenReturn(Optional.of(existingPrice));

        // Act
        priceService.updatePrice(priceRequest);

        // Assert
        verify(priceRepository, times(1)).save(existingPrice);
        assertEquals(new BigDecimal("100.00"), existingPrice.getPrice());
        assertEquals(new BigDecimal("80.00"), existingPrice.getSalePrice());
    }

    @Test
    void testUpdatePrice_PriceNotFound() {
        // Arrange
        PriceRequest priceRequest = new PriceRequest(1L, null, new BigDecimal("100.00"), new BigDecimal("80.00"), LocalDate.now(), LocalDate.now().plusDays(10), true);

        when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> priceService.updatePrice(priceRequest));
        assertEquals("Price not found", exception.getMessage());
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void testGetPriceByCourse_Success() {
        // Arrange
        Long courseId = 1L;
        PriceRequest priceRequest = new PriceRequest(null, courseId, null, null, null, null, null);
        Course course = Course.builder().id(courseId).build();
        Price price = Price.builder()
                .id(1L)
                .price(new BigDecimal("100.00"))
                .salePrice(new BigDecimal("80.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .isActive(true)
                .course(course)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(priceRepository.findByCourse(course)).thenReturn(Optional.of(price));

        // Act
        PriceResponse result = priceService.getPriceByCourse(priceRequest);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals(new BigDecimal("80.00"), result.getSalePrice());
    }



}
