package liliana.serverreactnative.controller;


import liliana.serverreactnative.model.dto.response.ApiResponse;
import liliana.serverreactnative.model.dto.response.ProductResponse;
import liliana.serverreactnative.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products
     * Lấy tất cả sản phẩm (Có thể thêm phân trang, lọc sau)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();

        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Lấy danh sách sản phẩm thành công")
                .timestamp(LocalDateTime.now())
                .data(products)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /api/products/{id}
     * Lấy chi tiết sản phẩm
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetails(@PathVariable Integer id) {
        ProductResponse product = productService.getProductById(id);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Lấy chi tiết sản phẩm thành công")
                .timestamp(LocalDateTime.now())
                .data(product)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
