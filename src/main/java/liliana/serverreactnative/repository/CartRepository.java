package liliana.serverreactnative.repository;

import liliana.serverreactnative.model.entity.Cart;
import liliana.serverreactnative.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    // Tìm giỏ hàng theo User
    Optional<Cart> findByUser(User user);
}
