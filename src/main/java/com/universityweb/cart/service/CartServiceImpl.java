package com.universityweb.cart.service;

import com.universityweb.bundle.Bundle;
import com.universityweb.bundle.service.BundleService;
import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.exception.CartItemAlreadyExistsException;
import com.universityweb.cart.exception.CartItemNotFoundException;
import com.universityweb.cart.mapper.CartItemMapper;
import com.universityweb.cart.mapper.CartMapper;
import com.universityweb.cart.repository.CartItemRepos;
import com.universityweb.cart.repository.CartRepos;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.response.CartResponse;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.price.entity.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartRepos cartRepos;
    private final CartItemRepos cartItemRepos;
    private final UserService userService;
    private final CourseService courseService;
    private final BundleService bundleService;

    @Override
    public CartItemResponse addItemToCart(String username, Long courseId, Long bundleId) {
        boolean existNotInCart = existNotInCart(username, courseId);
        boolean isCourseInBundle = true;
        if (bundleId != null) {
            isCourseInBundle =  bundleService.isCourseInBundle(courseId, bundleId);
        }
        boolean isValid = existNotInCart && isCourseInBundle;
        if (!isValid) {
            throw new CartItemAlreadyExistsException("Item already exists");
        }

        Cart cart = getOrCreateCart(username);
        Course course = courseService.getEntityById(courseId);

        Price price = course.getPrice();
        BigDecimal priceToUse = price.getApplicablePrice();

        CartItem cartItem = CartItem.builder()
                .status(CartItem.EStatus.ACTIVE)
                .price(priceToUse)
                .updatedAt(LocalDateTime.now())
                .course(course)
                .bundleId(bundleId)
                .cart(cart)
                .build();

        CartItem savedCartItem = cartItemRepos.save(cartItem);
        CartItemResponse cartItemResponse = cartItemMapper.toDTO(savedCartItem);
        updateCartTime(cart);
        return cartItemResponse;
    }

    @Override
    public CartItemResponse updateItem(Long cartItemId) {
        CartItem cartItem = getCartItemByCartItemId(cartItemId);
        CartItemResponse cartItemResponse = cartItemMapper.toDTO(cartItem);
        updateCartTime(cartItem.getCart());
        return cartItemResponse;
    }

    @Override
    public boolean removeItemFromCart(Long cartItemId) {
        Cart cart = getCartByCartItemId(cartItemId);
        Long cartId = cart.getId();
        String cartItemNotFoundMsg =
                String.format("Could not find any cart item with cartItemId=%d", cartId);
        CartItem cartItem = cartItemRepos
                .findByCartItemById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemNotFoundMsg));

        CartItem.EStatus status = cartItem.getStatus();
        if (status == CartItem.EStatus.ACTIVE) {
            cartItem.setStatus(CartItem.EStatus.DELETED);
            cartItem.setUpdatedAt(LocalDateTime.now());
        }

        cartItemRepos.save(cartItem);
        updateCartTime(cart);
        return true;
    }

    @Override
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        List<CartItem> cartItems = cartItemRepos
                .findByCartIdAndStatus(cart.getId(), CartItem.EStatus.ACTIVE);

        cartItems.forEach(item -> {
            item.setStatus(CartItem.EStatus.DELETED);
            item.setUpdatedAt(LocalDateTime.now());
        });

        cartItemRepos.saveAll(cartItems);
        updateCartTime(cart);
    }

    @Override
    public CartResponse getCartByUsername(String username) {
        Cart cart = getCartEntityByUsername(username);
        CartResponse cartResponse = cartMapper.toDTO(cart);
        BigDecimal totalAmount = cartResponse.getTotalAmount();
        cartResponse.setTotalAmount(totalAmount);

        try {
            for (int i = 0; i < cart.getItems().size(); i++) {
                CartItem c1 = cart.getItems().get(i);
                CartItemResponse c2 = cartResponse.getItems().get(i);
                c2.setBundleId(c1.getBundleId());
            }
        } catch (Exception e) {
        }

        return cartResponse;
    }

    @Override
    public Cart getCartEntityByUsername(String username) {
        Cart cart = cartRepos.findByUsername(username)
                .orElse(null);

        if (cart == null) {
            cart = getOrCreateCart(username);
        }

        List<CartItem> cartItems = getCartItemsToDisplay(username);
        cart.setItems(cartItems);

        return cart;
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

    @Override
    public BigDecimal getTotalAmountOfCart(String username) {
        List<CartItem> cartItems = getCartItemsToDisplay(username);
        List<CartItemResponse> cartItemResponses = cartItemMapper.toDTOs(cartItems);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemResponse cartItemResponse : cartItemResponses) {
            BigDecimal itemPrice = cartItemResponse.getPrice();
            totalAmount = totalAmount.add(itemPrice);
        }
        return totalAmount;
    }

    @Override
    public boolean existNotInCart(String username, Long courseId) {
        List<CartItem> cartItems = cartItemRepos.findByUsernameAndCourseId(username, courseId);
        return cartItems.stream()
                .noneMatch(cartItem -> cartItem.getStatus() == CartItem.EStatus.ACTIVE);
    }

    @Override
    public List<CartItemResponse> addBundleToCart(String username, Long bundleId) {
        Bundle bundle = bundleService.getEntityById(bundleId);
        List<Long> courseIds = bundle.getCourses().stream()
                .map(Course::getId)
                .toList();
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        courseIds.forEach(courseId -> {
            CartItemResponse cartItemResponse = addItemToCart(username, courseId, bundleId);
            cartItemResponses.add(cartItemResponse);
        });
        return cartItemResponses;
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

    private void updateCartTime(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepos.save(cart);
    }
}
