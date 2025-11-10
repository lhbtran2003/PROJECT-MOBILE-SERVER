package liliana.serverreactnative.model.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String message;
    private String username;
    private String email;
    // Có thể thêm JWT token nếu bạn triển khai bảo mật
    // private String token;
}
