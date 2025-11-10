package liliana.serverreactnative.model.dto.response;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Integer cartId;
    private List<CartItemResponse> items;
    private BigDecimal subTotal; // Tổng tiền của tất cả items
    private Integer totalItems; // Tổng số lượng sản phẩm
}