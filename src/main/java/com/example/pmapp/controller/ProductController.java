package com.example.pmapp.controller;

import com.example.pmapp.dto.Product;
import com.example.pmapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // 상품 등록 처리
    @GetMapping("/{id}/edit")
    public String create(@ModelAttribute Product product){
        productRepository.save(product);
        return "redirect:/products";
    }

    // 상품 수정
    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable Long id, Model model){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다:" + id));
        model.addAttribute("product", product);
        return "product/form";
    }

    // 상품 수정 처리
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Product product){
        product.setId(id);
        productRepository.save(product);
        return "redirect:/products";
    }

    // 상품 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        productRepository.deleteById(id);
        return "redirect:/products";
    }
}
