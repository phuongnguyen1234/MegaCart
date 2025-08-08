package com.megacart.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XacThucOtpRequest {
    @Email(message = "Email không hợp lệ")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 6, message = "Mã OTP phải có 6 chữ số")
    private String otp;
}