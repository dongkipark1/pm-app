package com.example.pmapp.controller;

import com.example.pmapp.component.FileStorageService;
import com.example.pmapp.dto.Product;
import com.example.pmapp.dto.ProductVariant;
import com.example.pmapp.repository.ProductRepository;
import com.example.pmapp.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final FileStorageService fileStorageService;

    /** 1) 전체 상품 목록 **/
    @GetMapping
    public String list(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "product/list";
    }

    /** 2) 신규 등록 폼 **/
    @GetMapping("/create-form")
    public String createForm(Model model){
        model.addAttribute("id", null);
        model.addAttribute("name", "");
        model.addAttribute("price", 0);
        model.addAttribute("description", "");
        model.addAttribute("stock", 0);
        model.addAttribute("imageUrl", null);      // 이미지 URL 초기화
        return "product/form";
    }

    /** 3) 등록 처리 (이미지 업로드 포함) **/
    @PostMapping("/create")
    public String create(
            @ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {
        if (!imageFile.isEmpty()) {
            String filename = fileStorageService.store(imageFile);
            product.setImageUrl("/uploads/" + filename);
        }
        Product saved = productRepository.save(product);
        return "redirect:/products/" + saved.getId() + "?remindOptions=true";
    }

    /** 4) 수정 폼 **/
    @GetMapping("/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model model){
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        model.addAttribute("id",          p.getId());
        model.addAttribute("name",        p.getName());
        model.addAttribute("price",       p.getPrice());
        model.addAttribute("description", p.getDescription());
        model.addAttribute("stock",       p.getStock());
        model.addAttribute("imageUrl",    p.getImageUrl());
        return "product/form";
    }

    /** 5) 수정 처리 **/
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @ModelAttribute Product form,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));

        existing.setName(form.getName());
        existing.setPrice(form.getPrice());
        existing.setDescription(form.getDescription());
        existing.setStock(form.getStock());

        if (!imageFile.isEmpty()){
            String filename = fileStorageService.store(imageFile);
            existing.setImageUrl("/uploads/" + filename);
        }

        productRepository.save(existing);
        return "redirect:/products";
    }

    /** 6) 삭제 처리 **/
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    /** 7) 상세 + 옵션 목록 **/
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(value = "remindOptions", required = false) Boolean remindOptions,
            Model model
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        List<ProductVariant> variants = productVariantRepository.findByProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("sizes", List.of("XS","S","M","L","XL"));
        model.addAttribute("variantCount", variants.size());         // ← 여기 추가
        model.addAttribute("variantForm", new ProductVariant());
        model.addAttribute("showOptionModal", Boolean.TRUE.equals(remindOptions));
        return "product/detail";
    }

    /** 8) 옵션 등록 **/
    @PostMapping("/{id}/variants")
    public String addVariant(
            @PathVariable Long id,
            @ModelAttribute("variantForm") ProductVariant variantForm,
            RedirectAttributes redirectAttrs
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품: " + id));

        // 기존 옵션들의 총 수량
        List<ProductVariant> existing = productVariantRepository.findByProductId(id);
        int sum = existing.stream()
                .mapToInt(ProductVariant::getStockQuantity)
                .sum();

        // 새로 추가하려는 옵션 수량
        int incoming = variantForm.getStockQuantity();

        if (sum + incoming > product.getStock()) {
            // 재고 초과 시, 상세페이지로 redirect하면서 에러 플래시
            redirectAttrs.addFlashAttribute("errorMessage",
                    "옵션 수량 합계(" + (sum + incoming) + ")가 제품 재고(" + product.getStock() + ")를 초과합니다.");
            return "redirect:/products/" + id + "?remindOptions=true";
        }

        // 재고가 충분하면 저장
        variantForm.setId(null);
        variantForm.setProduct(product);
        productVariantRepository.save(variantForm);

        return "redirect:/products/" + id;
    }

    /** 9) 옵션 삭제 **/
    @PostMapping("/{id}/variants/{variantId}/delete")
    public String deleteVariant(
            @PathVariable Long id,
            @PathVariable Long variantId
    ) {
        productVariantRepository.deleteById(variantId);
        return "redirect:/products/" + id;  // 빠진 슬래시 추가!
    }
}
