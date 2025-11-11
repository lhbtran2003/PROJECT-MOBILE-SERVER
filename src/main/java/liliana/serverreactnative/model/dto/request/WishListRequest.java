package liliana.serverreactnative.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WishListRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    private Integer productId;
}