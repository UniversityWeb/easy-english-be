package com.universityweb.cart.service;

import com.universityweb.cart.CartItemMapper;
import com.universityweb.cart.CartRepos;
import com.universityweb.cart.model.CartItem;
import com.universityweb.cart.model.CartItemDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.model.Course;
import com.universityweb.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper = CartItemMapper.INSTANCE;

    @Autowired
    private CartRepos cartRepos;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepos userRepos;

    @Override
    public List<CartItemDTO> getCartItems(String username) {
        List<CartItem> cartItems = cartRepos.findByUserUsernameAndStatus(username, CartItem.EStatus.ACTIVE);
        return cartItemMapper.toDTOs(cartItems);
    }

    @Override
    public CartItemDTO addItemToCart(String username, Long courseId) {
        Optional<CartItem> existingCartItem = cartRepos.findByUsernameAndCourseId(username, courseId);
        Course course = courseService.getCourseById(courseId.intValue());

        CartItem cartItem = existingCartItem.orElseGet(() -> {
            User user = userService.loadUserByUsername(username);

            return CartItem.builder()
                    .status(CartItem.EStatus.ACTIVE)
                    .price(new BigDecimal(course.getPrice()))
                    .discountPercent(BigDecimal.ZERO)
                    .user(user)
                    .course(course)
                    .build();
        });

        existingCartItem.ifPresent(item -> {
            BigDecimal discount = calculateDiscount(new BigDecimal(course.getPrice()), item.getPrice());

            item.setStatus(CartItem.EStatus.ACTIVE);
            item.setDiscountPercent(discount);
        });

        CartItem savedCartItem = cartRepos.save(cartItem);
        return cartItemMapper.toDTO(savedCartItem);
    }

    @Override
    public boolean removeItemFromCart(String username, Long courseId) {
        List<CartItem> cartItems = cartRepos.findByUserUsernameAndStatus(username, CartItem.EStatus.ACTIVE);
        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getUser().getUsername().equals(username) && item.getCourse().getId() == courseId)
                .findFirst()
                .orElse(null);

        if (cartItem == null) {
            return false;
        }

        cartItem.setStatus(CartItem.EStatus.DELETED);
        cartRepos.save(cartItem);
        return true;
    }

    @Override
    public void clearCart(String username) {
        List<CartItem> cartItems = cartRepos.findAll()
                .stream()
                .filter(item -> item.getUser().getUsername().equals(username))
                .collect(Collectors.toList());

        cartItems.forEach(item -> item.setStatus(CartItem.EStatus.DELETED));
        cartRepos.saveAll(cartItems);
    }

    private BigDecimal calculateDiscount(BigDecimal coursePrice, BigDecimal cartItemPrice) {
        if (coursePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = coursePrice.subtract(cartItemPrice);
        return discountAmount.divide(coursePrice, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}
