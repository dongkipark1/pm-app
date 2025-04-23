package com.example.pmapp.controller;

import com.example.pmapp.dto.Product;
import com.example.pmapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    // 전체 상품 목록
    @GetMapping
    public String list(Model model){
        List<Product> products = productRepository.findAll();
        model.addAttribute("product", products);
        return "product/list";
    }

    // 상품 등록
    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("product", new Product());
        return "product/form";
    }
}
