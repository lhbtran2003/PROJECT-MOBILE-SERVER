package liliana.serverreactnative.model.dto.response;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Integer cartItemId;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice; // Tổng tiền = quantity * price
}