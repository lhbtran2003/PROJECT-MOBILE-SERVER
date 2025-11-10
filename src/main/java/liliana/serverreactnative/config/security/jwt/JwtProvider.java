package liliana.serverreactnative.config.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    // định nghĩa cách tạo token
    public String generateAccessToken(UserDetails userDetails) {
        Date today = new Date();
        return Jwts.builder()
//                .claim("roles", userDetails.getAuthorities())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(today)
                .setExpiration(new Date(today.getTime() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // validat token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT: message error expired:", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT: message error unsupported:", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT: message error not formated:", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT: message error signature not math:", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT: message claims empty or argument invalid: ", e.getMessage());
        }
        return false;
    }

    // lấy thông tin từ token
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
}
