package com.example.bookingbadminton.model.dto.request.auth;

import com.example.bookingbadminton.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequestDto {

    @Schema(description = "Tài khoản", example = "quanducbui2017@gmail.com")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Size(max = 50)
    String username;

    @Schema(description = "Mật khẩu", example = "Quankane1905@")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    @Size(min = 6, max = 300)
    String password;

}
