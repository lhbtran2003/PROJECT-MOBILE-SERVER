package liliana.serverreactnative.service;

import liliana.serverreactnative.config.exception.BadRequestException;
import liliana.serverreactnative.config.exception.NotFoundException;
import liliana.serverreactnative.model.dto.request.CartItemRequest;
import liliana.serverreactnative.model.dto.response.CartItemResponse;
import liliana.serverreactnative.model.dto.response.CartResponse;
import liliana.serverreactnative.model.entity.Cart;
import liliana.serverreactnative.model.entity.CartItem;
import liliana.serverreactnative.model.entity.Product;
import liliana.serverreactnative.model.entity.User;
import liliana.serverreactnative.repository.CartItemRepository;
import liliana.serverreactnative.repository.CartRepository;
import liliana.serverreactnative.repository.ProductRepository;
import liliana.serverreactnative.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Giả định bạn có UserRepository

    // --- Phương thức tìm User hiện tại ---
    private User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Giả định User Entity có method findByUsername
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Người dùng không tìm thấy trong DB."));
    }

    // --- Phương thức tìm hoặc tạo Cart ---
    private Cart getOrCreateUserCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(cart));
    }

    // --- MAPPER ---
    private CartItemResponse mapCartItemToResponse(CartItem item) {
        // Tính tổng tiền cho item này
        BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(item.getCartItemId())
                .productId(item.getProduct().getProductId())
                .productName(item.getProduct().getProductName())
                .productImageUrl(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(totalPrice)
                .build();
    }

    private CartResponse mapCartToResponse(Cart cart) {
        List<CartItemResponse> itemsResponse = cart.getCartItems().stream()
                .map(this::mapCartItemToResponse)
                .collect(Collectors.toList());

        // Tính tổng subTotal và tổng số lượng
        BigDecimal subTotal = itemsResponse.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemsResponse.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .items(itemsResponse)
                .subTotal(subTotal)
                .totalItems(totalItems)
                .build();
    }

    // -------------------------------------------------------------------------------------------------
    //                                     CÁC CHỨC NĂNG API
    // -------------------------------------------------------------------------------------------------

    /**
     * 1. Thêm hoặc cập nhật sản phẩm vào giỏ hàng
     */
    public CartResponse addOrUpdateCartItem(CartItemRequest request) {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        // 1. Tìm Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại."));

        // 2. Tìm CartItem đã tồn tại
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItemOpt.isPresent()) {
            // Cập nhật số lượng (Thêm vào số lượng hiện tại)
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());

            // Xóa item nếu số lượng bị kéo về <= 0
            if (item.getQuantity() <= 0) {
                cartItemRepository.delete(item);
            } else {
                cartItemRepository.save(item);
            }
        } else  {
            // Thêm mới CartItem
            CartItem newItem = new CartItem();
            newItem.setQuantity(1);
            newItem.setProduct(product);
            newItem.setCart(cart);
            newItem.setPrice(product.getPrice());
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        // Cần tải lại Cart để có danh sách CartItem cập nhật
        cart = cartRepository.findByUser(user).get();

        return mapCartToResponse(cart);
    }

    /**
     * 2. Xem danh sách sản phẩm trong giỏ hàng
     */
    @Transactional()
    public CartResponse getCart() {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        // Tải danh sách CartItem (đã EAGER load Product)
        if (cart.getCartItems().isEmpty()) {
            // Trả về giỏ hàng rỗng
            return CartResponse.builder().cartId(cart.getCartId()).items(List.of()).subTotal(BigDecimal.ZERO).totalItems(0).build();
        }

        return mapCartToResponse(cart);
    }

    /**
     * 3. Xóa một CartItem (xóa hoàn toàn sản phẩm khỏi giỏ)
     */
    public CartResponse removeCartItem(Integer cartItemId) {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        // 1. Tìm CartItem
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm trong giỏ không tồn tại."));

        // 2. Kiểm tra quyền sở hữu CartItem này
        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Bạn không có quyền xóa sản phẩm này.");
        }

        // 3. Xóa và cập nhật giỏ hàng
        cartItemRepository.delete(item);

        cart.getCartItems().remove(item); // <-- THÊM DÒNG NÀY

        // Cần tải lại Cart để có danh sách CartItem cập nhật
        cart = cartRepository.findByUser(user).get();

        return mapCartToResponse(cart);
    }

    /**
     * 4. Xóa toàn bộ giỏ hàng
     */
    public CartResponse clearCart() {
        User user = getCurrentAuthenticatedUser();
        Cart cart = getOrCreateUserCart(user);

        // Xóa tất cả CartItem thuộc Cart này
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear(); // Cập nhật danh sách trong entity
        cartRepository.save(cart); // Lưu thay đổi trên Cart

        return mapCartToResponse(cart); // Trả về giỏ hàng rỗng
    }
}