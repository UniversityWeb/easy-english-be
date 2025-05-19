package com.universityweb.common.uc;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.mapper.CartItemMapper;
import com.universityweb.cart.mapper.CartMapper;
import com.universityweb.cart.repository.CartItemRepos;
import com.universityweb.cart.repository.CartRepos;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.service.CartServiceImpl;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.cart.exception.CartItemAlreadyExistsException;
import com.universityweb.price.entity.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_007_CartManagement_Tests {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartRepos cartRepos;

    @Mock
    private CartItemRepos cartItemRepos;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItemToCart_Success_CART_MANAGEMENT_POS_001() {
        // Arrange
        String username = "john_doe";
        Long courseId = 1L;

        Cart cart = Cart.builder()
                .id(1L)
                .updatedAt(LocalDateTime.now())
                .build();

        Course course = Course.builder()
                .id(courseId)
                .price(Price.builder()
                        .price(BigDecimal.valueOf(100))
                        .build())
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .status(CartItem.EStatus.ACTIVE)
                .price(BigDecimal.valueOf(100))
                .discountPercent(BigDecimal.ZERO)
                .updatedAt(LocalDateTime.now())
                .course(course)
                .cart(cart)
                .build();

        CartItemResponse cartItemResponse = CartItemResponse.builder()
                .id(1L)
                .price(BigDecimal.valueOf(100))
                .discountPercent(BigDecimal.ZERO)
                .status(CartItem.EStatus.ACTIVE)
                .build();

        when(cartItemRepos.findByUsernameAndCourseId(username, courseId)).thenReturn(List.of());
        when(cartRepos.findByUsername(username)).thenReturn(Optional.of(cart));
        when(courseService.getEntityById(courseId)).thenReturn(course);
        when(cartItemRepos.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDTO(cartItem)).thenReturn(cartItemResponse);

        // Act
        CartItemResponse result = cartService.addItemToCart(username, courseId, null);

        // Assert
        assertNotNull(result);
        assertEquals(cartItemResponse.getId(), result.getId());
        assertEquals(cartItemResponse.getPrice(), result.getPrice());
        verify(cartRepos, times(1)).findByUsername(username);
        verify(courseService, times(1)).getEntityById(courseId);
        verify(cartItemRepos, times(1)).save(any(CartItem.class));
        verify(cartItemMapper, times(1)).toDTO(cartItem);
    }

    @Test
    void testAddItemToCart_AlreadyExists_CART_MANAGEMENT_NEG_002() {
        // Arrange
        String username = "john_doe";
        Long courseId = 1L;

        CartItem existingCartItem = CartItem.builder()
                .id(1L)
                .status(CartItem.EStatus.ACTIVE)
                .build();

        when(cartItemRepos.findByUsernameAndCourseId(username, courseId)).thenReturn(List.of(existingCartItem));

        // Act & Assert
        CartItemAlreadyExistsException exception = assertThrows(
                CartItemAlreadyExistsException.class,
                () -> cartService.addItemToCart(username, courseId, null)
        );

        assertEquals("Item already exists", exception.getMessage());
        verify(cartRepos, never()).findByUsername(username);
        verify(courseService, never()).getEntityById(courseId);
        verify(cartItemRepos, never()).save(any(CartItem.class));
    }

    @Test
    void testRemoveItemFromCart_Success_CART_MANAGEMENT_POS_003() {
        // Arrange
        Long cartItemId = 1L;

        Cart cart = Cart.builder()
                .id(1L)
                .updatedAt(LocalDateTime.now())
                .build();

        CartItem cartItem = CartItem.builder()
                .id(cartItemId)
                .status(CartItem.EStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .cart(cart)
                .build();

        // Mock cartItemRepos to return the CartItem
        when(cartItemRepos.findByCartItemById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepos.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        // Mock cartRepos to return the Cart
        when(cartRepos.findById(cart.getId())).thenReturn(Optional.of(cart));

        // Act
        boolean result = cartService.removeItemFromCart(cartItemId);

        // Assert
        assertTrue(result);
        assertEquals(CartItem.EStatus.DELETED, cartItem.getStatus());
        verify(cartItemRepos, times(1)).findByCartItemById(cartItemId);
        verify(cartItemRepos, times(1)).save(cartItem);
        verify(cartRepos, times(1)).save(cart);
    }
}