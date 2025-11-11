package liliana.serverreactnative.controller;


import liliana.serverreactnative.model.dto.request.WishListRequest;
import liliana.serverreactnative.model.dto.response.ApiResponse;
import liliana.serverreactnative.model.dto.response.ProductResponse;
import liliana.serverreactnative.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addProductToWishList(@Valid @RequestBody WishListRequest request) {
        wishListService.addProductToWishList(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Thêm sản phẩm vào danh sách yêu thích thành công.")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeProductFromWishList(@PathVariable Integer productId) {
        wishListService.removeProductFromWishList(productId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa sản phẩm khỏi danh sách yêu thích thành công.")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishList() {
        List<ProductResponse> wishList = wishListService.getWishList();

        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Lấy danh sách yêu thích thành công.")
                .data(wishList)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}