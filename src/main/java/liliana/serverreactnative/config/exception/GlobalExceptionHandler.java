package liliana.serverreactnative.config.exception;


import liliana.serverreactnative.model.dto.response.ApiResponse;
import liliana.serverreactnative.model.dto.response.FieldErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRoleException(InvalidRoleException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .success(false)
                .message("Có lỗi xảy ra trong quá trình kiểm tra dữ liệu")
                .errors(errorList)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConflictDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictDataException(ConflictDataException ex) {

        // Tạo danh sách lỗi chỉ chứa FieldErrorResponse từ Exception
        List<FieldErrorResponse> errorList = Collections.singletonList(ex.getErrorResponse());

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .message("Đã xảy ra lỗi xung đột dữ liệu") // Thông báo lỗi chung
                .timestamp(LocalDateTime.now())
                .errors(errorList) // Đặt lỗi chi tiết vào trường 'errors'
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT); // 409 Conflict
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequestException(BadRequestException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }


}
