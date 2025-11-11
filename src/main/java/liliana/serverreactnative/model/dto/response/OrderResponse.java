package liliana.serverreactnative.model.dto.response;


import liliana.serverreactnative.model.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Integer orderId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;

    // Thông tin giao hàng
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private List<OrderItemResponse> items;
}