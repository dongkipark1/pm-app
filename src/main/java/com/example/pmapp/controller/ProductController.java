package com.example.pmapp.controller;

import com.example.pmapp.dto.Product;
import com.example.pmapp.dto.ProductVariant;
import com.example.pmapp.repository.ProductRepository;
import com.example.pmapp.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    // 전체 상품 목록
    @GetMapping
    public String list(Model model){
        List<Product> products = productRepository.findAll();
        model.addAttribute("product", products);
        return "product/list";
    }

    // 상품 등록
    @GetMapping("/create-form")
    public String createForm(Model model){
        model.addAttribute("id", null);
        model.addAttribute("name", "");
        model.addAttribute("price", 0);
        model.addAttribute("description", "");
        model.addAttribute("stock", 0);
        return "product/form";
    }

    // 상품 등록 처리
    @PostMapping("/create")
    public String create(@ModelAttribute Product product){
        productRepository.save(product);
        return "redirect:/products";
    }

    // 상품 수정
    @GetMapping("/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model model){
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        model.addAttribute("id",          p.getId());
        model.addAttribute("name",        p.getName());
        model.addAttribute("price",       p.getPrice());
        model.addAttribute("description", p.getDescription());
        model.addAttribute("stock",       p.getStock());
        return "product/form";
    }

    // 상품 수정 처리
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Product product){
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품: " + id));

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        existing.setStock(product.getStock());

        productRepository.save(existing);
        return "redirect:/products";
    }

    // 상품 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    // 상품 상세 + 옵션 목록
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품" + id));
        List<ProductVariant> variants = productVariantRepository.findByProductId(id);
        model.addAttribute("product", product);
        model.addAttribute("variants", variants);

        // 새 옵션 추가용 오브젝트
        model.addAttribute("variantForm", new ProductVariant());
        return "product/detail";
    }

    // 옵션 등록
    @PostMapping("/{id}/variants")
    public String addVariant(@PathVariable Long id, @ModelAttribute("variantForm") ProductVariant variantForm){
        Product product = productRepository.findById(id)
                .orElseThrow();

        variantForm.setProduct(product);
        productVariantRepository.save(variantForm);
        return "redirect:/products/" + id;
    }

    // 옵션 삭제
    @PostMapping("/{id}/variants/{variantId}/delete")
    public String deleteVariant(@PathVariable Long id, @PathVariable Long variantId){
        productVariantRepository.deleteById(variantId);
        return "redirect:/products" + id;
    }
}
