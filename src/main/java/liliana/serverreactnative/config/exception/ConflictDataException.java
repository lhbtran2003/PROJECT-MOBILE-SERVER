package liliana.serverreactnative.config.exception;

import liliana.serverreactnative.model.dto.response.FieldErrorResponse;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ConflictDataException extends RuntimeException {
    // Thuộc tính để chứa lỗi chi tiết
    private final FieldErrorResponse errorResponse;

    // Constructor nhận thông tin về lỗi trường cụ thể
    public ConflictDataException(String field, String message) {
        super(message); // Truyền thông báo chung (không bắt buộc)
        this.errorResponse = new FieldErrorResponse(field, message);
    }
}
