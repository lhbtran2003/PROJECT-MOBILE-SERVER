package liliana.serverreactnative.service;

import liliana.serverreactnative.config.exception.BadRequestException;
import liliana.serverreactnative.config.exception.ConflictDataException;
import liliana.serverreactnative.config.security.jwt.JwtProvider;
import liliana.serverreactnative.config.security.principle.UserDetail;
import liliana.serverreactnative.model.dto.request.LoginRequest;
import liliana.serverreactnative.model.dto.response.AuthResponse;
import liliana.serverreactnative.model.dto.request.RegisterRequest;
import liliana.serverreactnative.model.dto.response.LoginResponse;
import liliana.serverreactnative.model.entity.Role;
import liliana.serverreactnative.model.entity.User;
import liliana.serverreactnative.repository.RoleRepository;
import liliana.serverreactnative.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Cần cấu hình Bean này trong dự án
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Kiểm tra tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictDataException("username","Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictDataException("email","Email đã được sử dụng");
        }

        // 2. Tìm Role (mặc định là "USER")
        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BadRequestException("Vai trò mặc định không tồn tại"));

        // 3. Tạo đối tượng User
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Mã hóa mật khẩu
        newUser.setEmail(request.getEmail());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setRole(defaultRole); // Thiết lập vai trò mặc định

        // 4. Lưu vào cơ sở dữ liệu
        User savedUser = userRepository.save(newUser);

        // 5. Trả về Response
        return AuthResponse.builder()
                .message("Đăng ký tài khoản thành công")
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // 1. Thực hiện xác thực (Authentication)
        // AuthenticationManager sẽ gọi UserDetailsService để load user
        // và so sánh mật khẩu bằng PasswordEncoder.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Lưu đối tượng Authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Tạo JWT Token
        String jwtToken = jwtProvider.generateAccessToken((UserDetail) authentication.getPrincipal());

        // 4. Tìm kiếm thông tin người dùng để trả về
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại: " + loginRequest.getUsername()));

        // 5. Trả về Token và thông tin cơ bản của người dùng
        return LoginResponse.builder()
                .authenticationToken(jwtToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}