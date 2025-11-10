package liliana.serverreactnative.repository;

import liliana.serverreactnative.model.entity.Cart;
import liliana.serverreactnative.model.entity.CartItem;
import liliana.serverreactnative.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // Tìm CartItem theo Cart và Product
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // Xóa tất cả CartItem thuộc Cart này
    void deleteByCart(Cart cart);
}