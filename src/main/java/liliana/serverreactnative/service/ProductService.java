package liliana.serverreactnative.service;



import liliana.serverreactnative.config.exception.NotFoundException;
import liliana.serverreactnative.model.dto.response.ProductResponse;
import liliana.serverreactnative.model.entity.Product;
import liliana.serverreactnative.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    private ProductResponse mapProductToResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .build();
    }

    /**
     * Lấy danh sách tất cả sản phẩm
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết sản phẩm theo ID
     */
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại: " + productId));

        return mapProductToResponse(product);
    }
}