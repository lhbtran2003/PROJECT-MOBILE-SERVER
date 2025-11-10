package liliana.serverreactnative.controller;

import liliana.serverreactnative.model.dto.response.ApiResponse;
import liliana.serverreactnative.model.dto.request.CartItemRequest;
import liliana.serverreactnative.model.dto.response.CartResponse;
import liliana.serverreactnative.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // --- 1. Thêm sản phẩm vào giỏ hàng ---
    // Phương thức này cũng dùng để TĂNG/GIẢM số lượng sản phẩm
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addOrUpdateCartItem(@Valid @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addOrUpdateCartItem(request);

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Cập nhật giỏ hàng thành công")
                .data(cart)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 2. Xem giỏ hàng ---
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        CartResponse cart = cartService.getCart();

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Lấy thông tin giỏ hàng thành công")
                .data(cart)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 3. Xóa một CartItem (Dùng CartItemId) ---
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(@PathVariable Integer cartItemId) {
        CartResponse cart = cartService.removeCartItem(cartItemId);

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                .data(cart)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 4. Xóa toàn bộ giỏ hàng ---
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart() {
        CartResponse cart = cartService.clearCart();

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Xóa toàn bộ giỏ hàng thành công")
                .data(cart)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}