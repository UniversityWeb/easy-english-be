package com.universityweb.cart.service;

import com.universityweb.cart.mapper.CartItemMapper;
import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.repository.CartRepos;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.repository.CartItemRepos;
import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.response.CartResponse;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.model.Course;
import com.universityweb.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper = CartItemMapper.INSTANCE;

    @Autowired
    private CartRepos cartRepos;

    @Autowired
    private CartItemRepos cartItemRepos;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Override
    public CartResponse createCartForUser(String username) {
        User user = userService.loadUserByUsername(username);

        Optional<Cart> existingCartOpt = cartRepos.findByUsername(username);
        if (existingCartOpt.isEmpty()) {
            throw new RuntimeException("User already has an active cart");
        }

        return null;
    }

    @Override
    public List<CartItemResponse> getCartItems(String username) {
        List<CartItem> cartItems = cartItemRepos.findByUserUsernameAndStatus(CartItem.EStatus.ACTIVE);
        return cartItemMapper.toDTOs(cartItems);
    }

    @Override
    public CartItemResponse addItemToCart(String username, Long courseId) {
        Optional<CartItem> existingCartItem = cartItemRepos.findByUsernameAndCourseId(courseId);
        Course course = courseService.getCourseById(courseId.intValue());

        CartItem cartItem = existingCartItem.orElseGet(() -> {
            User user = userService.loadUserByUsername(username);

            return CartItem.builder()
                    .status(CartItem.EStatus.ACTIVE)
                    .price(new BigDecimal(course.getPrice()))
                    .discountPercent(BigDecimal.ZERO)
                    .updatedAt(LocalDateTime.now())
                    .course(course)
                    .build();
        });

        existingCartItem.ifPresent(item -> {
            BigDecimal discount = calculateDiscount(new BigDecimal(course.getPrice()), item.getPrice());

            item.setStatus(CartItem.EStatus.ACTIVE);
            item.setDiscountPercent(discount);
            item.setUpdatedAt(LocalDateTime.now());
        });

        CartItem savedCartItem = cartItemRepos.save(cartItem);
        return cartItemMapper.toDTO(savedCartItem);
    }

    @Override
    public boolean removeItemFromCart(String username, Long courseId) {
//        List<CartItem> cartItems = cartItemRepos.findByUserUsernameAndStatus(username, CartItem.EStatus.ACTIVE);
//        CartItem cartItem = cartItems.stream()
//                .filter(item -> item.getUser().getUsername().equals(username) && item.getCourse().getId() == courseId)
//                .findFirst()
//                .orElse(null);
//
//        if (cartItem == null) {
//            return false;
//        }
//
//        cartItem.setStatus(CartItem.EStatus.DELETED);
//        cartItem.setUpdatedAt(LocalDateTime.now());
//        cartItemRepos.save(cartItem);
        return true;
    }

    @Override
    public void clearCart(String username) {
//        List<CartItem> cartItems = cartItemRepos.findAll()
//                .stream()
//                .filter(item -> item.getUser().getUsername().equals(username))
//                .collect(Collectors.toList());
//        cartItems.forEach(item -> {
//            item.setStatus(CartItem.EStatus.DELETED);
//            item.setUpdatedAt(LocalDateTime.now());
//        });
//        cartItemRepos.saveAll(cartItems);
    }

    private BigDecimal calculateDiscount(BigDecimal coursePrice, BigDecimal cartItemPrice) {
        if (coursePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = coursePrice.subtract(cartItemPrice);
        return discountAmount.divide(coursePrice, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}
