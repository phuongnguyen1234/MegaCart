package com.megacart.exception.handler;

import com.megacart.dto.response.ErrorResponse;
import com.megacart.exception.InvalidOtpException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.megacart.exception.PhoneNumberAlreadyInUseException;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.exception.UserAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Xử lý các lỗi validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Xử lý lỗi validation cho các tham số trên URL (@RequestParam, @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Xử lý lỗi khi không thể chuyển đổi giá trị JSON thành Enum (ví dụ: "Giám đốc Marketing" -> ViTri)
    @ExceptionHandler(ValueInstantiationException.class)
    public ResponseEntity<ErrorResponse> handleValueInstantiationException(ValueInstantiationException ex, WebRequest request) {
        // Lấy thông báo lỗi gốc từ exception được bọc bên trong (IllegalArgumentException)
        // để trả về thông báo thân thiện cho người dùng.
        String message = ex.getCause() != null ? ex.getCause().getMessage() : "Dữ liệu không hợp lệ.";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Xử lý lỗi khi tham số trên URL có kiểu dữ liệu không đúng (ví dụ: ?trangThai=XYZ)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String paramName = ex.getName();
        Object value = ex.getValue();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Tham số '%s' có giá trị '%s' không hợp lệ. Kiểu dữ liệu yêu cầu là '%s'.", paramName, value, requiredType);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, WebRequest request) {
        String message = String.format("Tham số bắt buộc '%s' của yêu cầu bị thiếu.", ex.getParameterName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Xử lý lỗi nghiệp vụ như "hết hàng", "số lượng không hợp lệ"
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Xử lý lỗi không tìm thấy tài nguyên
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Xử lý lỗi xác thực (email không tồn tại, sai mật khẩu)
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(RuntimeException ex, WebRequest request) {
        // Luôn trả về thông báo chung chung để tăng cường bảo mật, tránh việc dò tài khoản
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác.", request);
    }

    // Xử lý lỗi tài nguyên đã tồn tại (email, sđt)
    @ExceptionHandler({UserAlreadyExistsException.class, PhoneNumberAlreadyInUseException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Xử lý lỗi OTP không hợp lệ
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtpException(InvalidOtpException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        String message = "Phần thân của yêu cầu (request body) bị thiếu hoặc có định dạng không đúng.";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Xử lý lỗi không có quyền truy cập (thường do @PreAuthorize)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập tài nguyên này.", request);
    }

    // Xử lý khi client gọi sai phương thức HTTP (GET, POST,...)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        String message = "Phương thức " + ex.getMethod() + " không được hỗ trợ cho yêu cầu này.";
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message, request);
    }

    // Xử lý lỗi vi phạm ràng buộc CSDL (ví dụ: xóa danh mục còn sản phẩm)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Thao tác không thể thực hiện do vi phạm ràng buộc dữ liệu. Có thể bạn đang cố xóa một đối tượng đang được sử dụng ở nơi khác.";
        log.error("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, message, request);
    }

    // Một "catch-all" handler để bắt tất cả các lỗi không mong muốn khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        // Ghi lại log của lỗi để đội ngũ phát triển có thể điều tra
        log.error("Đã xảy ra một lỗi không xác định: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Hệ thống đã gặp một lỗi không mong muốn. Vui lòng thử lại sau.", request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().timestamp(LocalDateTime.now()).status(status.value())
                .error(status.getReasonPhrase()).message(message)
                .path(request.getDescription(false).replace("uri=", "")).build();
        return new ResponseEntity<>(errorResponse, status);
    }
}