package com.example.pmapp.controller;

import com.example.pmapp.dto.Registration;
import com.example.pmapp.dto.User;
import com.example.pmapp.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm(){
        return "users/login-form";
    }

    @GetMapping("/join")
    public String joinForm(Model model){
        model.addAttribute("email", "");
        model.addAttribute("phoneNumber", "");
        model.addAttribute("errors", Collections.emptyMap());
        return "users/join-form";
    }

    @PostMapping("/join")
    public String join(
            @Valid @ModelAttribute("formList") Registration registration,
            BindingResult binding,
            Model model
    ) {
        // 비밀번호 일치 확인
        if (!registration.getPassword().equals(registration.getConfirmPassword())) {
            binding.rejectValue("confirmPassword", "mismatch", "비밀번호가 일치하지 않습니다");
        }
        // 이메일 중복 확인
        if (userRepository.existsByEmail(registration.getEmail())) {
            binding.rejectValue("email", "duplicate", "이미 사용 중인 이메일입니다");
        }

        if (binding.hasErrors()) {
            Map<String, String> errors = binding.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (existing, replacement) -> existing
                    ));

            model.addAttribute("email", registration.getEmail());
            model.addAttribute("phoneNumber", registration.getPhoneNumber());
            model.addAttribute("errors", errors);
            return "users/join-form";
        }

        // 정상 처리 시 사용자 저장
        User user = new User();
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setPhoneNumber(registration.getPhoneNumber());
        userRepository.save(user);

        return "redirect:/users/login?registered";
    }

    @GetMapping
    public String list(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/list";
    }
}
