package liliana.serverreactnative.service;



import liliana.serverreactnative.config.exception.BadRequestException;
import liliana.serverreactnative.config.exception.NotFoundException;
import liliana.serverreactnative.model.dto.request.WishListRequest;
import liliana.serverreactnative.model.dto.response.ProductResponse; // Giả định có DTO này
import liliana.serverreactnative.model.entity.Product;
import liliana.serverreactnative.model.entity.User;
import liliana.serverreactnative.model.entity.WishList;
import liliana.serverreactnative.repository.ProductRepository;
import liliana.serverreactnative.repository.UserRepository;
import liliana.serverreactnative.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    // Giả định bạn có một mapper từ Product Entity sang ProductResponse DTO


    // Helper (Sử dụng lại từ OrderService)
    private User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Lỗi xác thực người dùng."));
    }

    // -------------------------------------------------------------------------
    // 1. THÊM SẢN PHẨM VÀO DANH SÁCH YÊU THÍCH
    // -------------------------------------------------------------------------
    public WishList addProductToWishList(WishListRequest request) {
        User user = getCurrentAuthenticatedUser();

        // 1. Tìm Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại."));

        // 2. Kiểm tra nếu đã tồn tại
        if (wishListRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("Sản phẩm đã có trong danh sách yêu thích.");
        }

        // 3. Tạo WishList mới và lưu
        WishList newItem = new WishList();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setAddedAt(LocalDateTime.now());

        return wishListRepository.save(newItem);
    }

    // -------------------------------------------------------------------------
    // 2. XÓA SẢN PHẨM KHỎI DANH SÁCH YÊU THÍCH (Xóa theo ProductId)
    // -------------------------------------------------------------------------
    public void removeProductFromWishList(Integer productId) {
        User user = getCurrentAuthenticatedUser();

        // 1. Tìm Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại."));

        // 2. Tìm WishList Item
        WishList itemToRemove = wishListRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không có trong danh sách yêu thích của bạn."));

        // 3. Xóa
        wishListRepository.delete(itemToRemove);
    }

    // -------------------------------------------------------------------------
    // 3. HIỂN THỊ DANH SÁCH YÊU THÍCH
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<ProductResponse> getWishList() {
        User user = getCurrentAuthenticatedUser();

        List<WishList> wishItems = wishListRepository.findByUser(user);

        // Ánh xạ sang ProductResponse
        return wishItems.stream()
                .map(WishList::getProduct)
                .map(productService::mapProductToResponse) // Giả định mapper có phương thức toResponse
                .collect(Collectors.toList());
    }
}