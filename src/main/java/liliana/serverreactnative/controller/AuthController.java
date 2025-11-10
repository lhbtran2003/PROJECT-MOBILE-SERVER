package liliana.serverreactnative.controller;

import liliana.serverreactnative.model.dto.request.LoginRequest;
import liliana.serverreactnative.model.dto.response.AuthResponse;
import liliana.serverreactnative.model.dto.request.RegisterRequest;
import liliana.serverreactnative.model.dto.response.LoginResponse;
import liliana.serverreactnative.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Endpoint để đăng ký tài khoản mới.
     *
     * @param request Dữ liệu đăng ký từ người dùng
     * @return ResponseEntity chứa AuthResponse và HttpStatus.CREATED
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * API để người dùng đăng nhập
     * @param loginRequest DTO chứa tên đăng nhập và mật khẩu
     * @return ResponseEntity<LoginResponse> chứa JWT token và thông tin người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
