package liliana.serverreactnative.model.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter@Setter
@Builder
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    private List<FieldErrorResponse> errors;
    private LocalDateTime timestamp;
}