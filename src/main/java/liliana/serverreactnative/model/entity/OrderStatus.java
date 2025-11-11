package liliana.serverreactnative.model.entity;

public enum OrderStatus {
    PENDING,        // Đang chờ xác nhận (Mới tạo)
    PROCESSING,     // Đang xử lý / chuẩn bị hàng
    SHIPPING,       // Đang giao hàng
    DELIVERED,      // Đã giao hàng thành công
    CANCELLED       // Đã hủy
}