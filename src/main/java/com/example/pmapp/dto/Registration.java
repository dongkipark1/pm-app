package com.example.pmapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Registration {

    @NotBlank @Email(message = "올바른 이메일을 입력하세요")
    private String email;

    @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하세요")
    private String confirmPassword;

    @Pattern(regexp = "^01\\d-\\d{3,4}-\\d{4}$", message = "010-1234-5678 형태여야 합니다")
    private String phoneNumber;

}
