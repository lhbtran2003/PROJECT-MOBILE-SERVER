package liliana.serverreactnative.repository;

import liliana.serverreactnative.model.entity.Order;
import liliana.serverreactnative.model.entity.OrderStatus;
import liliana.serverreactnative.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByUserAndOrderStatusOrderByCreatedAtDesc(User user, OrderStatus status);
}
