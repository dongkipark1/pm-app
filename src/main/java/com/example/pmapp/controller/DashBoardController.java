package com.example.pmapp.controller;

import com.example.pmapp.repository.ProductRepository;
import com.example.pmapp.repository.StockRepository;
import com.example.pmapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashBoardController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model){
        long productCount = productRepository.count();
        long userCount = userRepository.count();
        long stockCount = stockRepository.count();

        model.addAttribute("productCount", productCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("stockCount", stockCount);

        return "dashboard";
    }
}
