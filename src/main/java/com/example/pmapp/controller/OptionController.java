package com.example.pmapp.controller;



import com.example.pmapp.dto.Product;
import com.example.pmapp.dto.ProductVariant;
import com.example.pmapp.repository.ProductRepository;
import com.example.pmapp.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/options")
public class OptionController {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    // 옵션 전용 페이지
    @GetMapping
    public String manage(@PathVariable Long productId, Model model) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다. : " + productId));
        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        model.addAttribute("product", p);
        model.addAttribute("variants", variants);
        model.addAttribute("variantForm", new ProductVariant());

        return "product/options";
    }

    // 옵션 추가
    @PostMapping
    public String add(@PathVariable Long productId, @ModelAttribute("variantForm") ProductVariant productVariant){
        Product p = productRepository.findById(productId).orElseThrow();
        productVariant.setProduct(p);
        productVariantRepository.save(productVariant);
        return "redirect:/products/" + productId + "/options";
    }

    // 옵션 삭제
    @PostMapping("/{variantId}/delete")
    public String delete(@PathVariable Long productId, @PathVariable Long variantId){
        productVariantRepository.deleteById(variantId);
        return "redirect:/products/" +productId + "/options";
    }
}
