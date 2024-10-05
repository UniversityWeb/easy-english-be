package com.universityweb.cart.service;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.exception.CartItemNotFoundException;
import com.universityweb.cart.mapper.CartItemMapper;
import com.universityweb.cart.mapper.CartMapper;
import com.universityweb.cart.repository.CartItemRepos;
import com.universityweb.cart.repository.CartRepos;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.response.CartResponse;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.model.Course;
import com.universityweb.course.model.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper = CartMapper.INSTANCE;
    private final CartItemMapper cartItemMapper = CartItemMapper.INSTANCE;
    private final CourseMapper courseMapper = CourseMapper.INSTANCE;

    @Autowired
    private CartRepos cartRepos;

    @Autowired
    private CartItemRepos cartItemRepos;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Override
    public CartItemResponse addItemToCart(String username, Long courseId) {
        Cart cart = getOrCreateCart(username);
        Course course = courseService.getCourseById((int) (long)courseId);

        CartItem cartItem = CartItem.builder()
                .status(CartItem.EStatus.ACTIVE)
                .price(new BigDecimal(course.getPrice()))
                .discountPercent(BigDecimal.ZERO)
                .updatedAt(LocalDateTime.now())
                .course(course)
                .cart(cart)
                .build();

        CartItem savedCartItem = cartItemRepos.save(cartItem);
        return cartItemMapper.toDTO(savedCartItem);
    }

    @Override
    public CartItemResponse updateItem(Long cartItemId) {
        CartItem cartItem = getCartItemByCartItemId(cartItemId);

        Long courseId = (long) cartItem.getCourse().getId();
        Course course = courseService.getCourseById((int) (long)courseId);

        BigDecimal discountPercent = calculateDiscount(new BigDecimal(course.getPrice()), cartItem.getPrice());
        cartItem.setDiscountPercent(discountPercent);

        return cartItemMapper.toDTO(cartItem);
    }

    @Override
    public boolean removeItemFromCart(String username, Long courseId) {
        Long cartId = getCartIdByUsername(username);
        String cartItemNotFoundMsg = String.format("Could not find any cart item with username=%s, courseId=%d",
                username, cartId);
        CartItem cartItem = cartItemRepos
                .findByCartIdAndCourseId(cartId, courseId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemNotFoundMsg));

        CartItem.EStatus status = cartItem.getStatus();
        if (status == CartItem.EStatus.ACTIVE || status == CartItem.EStatus.OUT_OF_STOCK) {
            cartItem.setStatus(CartItem.EStatus.DELETED);
            cartItem.setUpdatedAt(LocalDateTime.now());
        }

        cartItemRepos.save(cartItem);
        return true;
    }

    @Override
    public void clearCart(String username) {
        Long cartId = getCartIdByUsername(username);
        List<CartItem> cartItems = cartItemRepos
                .findByCartIdAndStatus(cartId, CartItem.EStatus.ACTIVE);

        cartItems.forEach(item -> {
            item.setStatus(CartItem.EStatus.DELETED);
            item.setUpdatedAt(LocalDateTime.now());
        });

        cartItemRepos.saveAll(cartItems);
    }

    @Override
    public CartResponse getCartByUsername(String username) {
        Cart cart = cartRepos.findByUsername(username)
                .orElse(null);

        if (cart == null) {
            cart = getOrCreateCart(username);
        }

        List<CartItem> cartItems = getCartItemsToDisplay(username);
        List<CartItemResponse> cartItemResponses = cartItemMapper.toDTOs(cartItems);

        CartResponse cartResponse = cartMapper.toDTO(cart);
        cartResponse.setItems(cartItemResponses);
        return cartResponse;
    }

    @Override
    public Cart getCartByCartItemId(Long cartItemId) {
        CartItem cartItem = getCartItemByCartItemId(cartItemId);
        return cartItem.getCart();
    }

    @Override
    public Integer countItems(String username) {
        Long cartId = getCartIdByUsername(username);
        return cartItemRepos.countByCartId(cartId);
    }

    private List<CartItem> getCartItemsToDisplay(String username) {
        Long cartId = getCartIdByUsername(username);
        return cartItemRepos.findItemsByCartIdToDisplay(cartId);
    }

    private BigDecimal calculateDiscount(BigDecimal coursePrice, BigDecimal cartItemPrice) {
        if (coursePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = coursePrice.subtract(cartItemPrice);
        return discountAmount.divide(coursePrice, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    private Cart getOrCreateCart(String username) {
        User user = userService.loadUserByUsername(username);

        Optional<Cart> existingCartOpt = cartRepos.findByUsername(username);
        if (existingCartOpt.isEmpty()) {
            Cart cart = Cart.builder()
                    .totalAmount(BigDecimal.ZERO)
                    .updatedAt(LocalDateTime.now())
                    .user(user)
                    .build();

            return cartRepos.save(cart);
        }

        return existingCartOpt.get();
    }

    private Long getCartIdByUsername(String username) {
        Cart cart = getOrCreateCart(username);
        return cart.getId();
    }

    private CartItem getCartItemByCartItemId(Long cartItemId) {
        String msg = "Could not find any cart item with id: " + cartItemId;
        return cartItemRepos.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(msg));
    }
}
