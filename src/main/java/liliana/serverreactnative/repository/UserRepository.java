package liliana.serverreactnative.repository;


import liliana.serverreactnative.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Kiểm tra xem username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Kiểm tra xem email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm kiếm User theo username (thường dùng cho logic đăng nhập)
    Optional<User> findByUsername(String username);
}