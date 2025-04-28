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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
        model.addAttribute("form", new Registration());
        return "users/join-form";
    }

    @PostMapping("/join")
    public String join(
            @Valid @ModelAttribute("form") Registration form,
            BindingResult binding
    ) {
        // 비밀번호 일치 확인
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            binding.rejectValue("confirmPassword","mismatch","비밀번호가 일치하지 않습니다");
        }
        // 이메일 중복 확인
        if (userRepository.existsByEmail(form.getEmail())) {
            binding.rejectValue("email","duplicate","이미 사용 중인 이메일입니다");
        }
        if (binding.hasErrors()) {
            return "users/join-form";
        }

        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setPhoneNumber(form.getPhoneNumber());
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
