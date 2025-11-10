package liliana.serverreactnative.model.dto.response;

// src/main/java/com/liliana/auth/dto/LoginResponse.java

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String authenticationToken;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
