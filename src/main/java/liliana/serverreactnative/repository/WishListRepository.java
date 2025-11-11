package liliana.serverreactnative.repository;

import liliana.serverreactnative.model.entity.Product;
import liliana.serverreactnative.model.entity.User;
import liliana.serverreactnative.model.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {

    List<WishList> findByUser(User user);

    Optional<WishList> findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);
}