package com.example.pmapp.controller;

import com.example.pmapp.component.FileStorageService;
import com.example.pmapp.dto.Product;
import com.example.pmapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepo;
    private final FileStorageService fileStorageService;

    // 1) 전체 목록
    @GetMapping
    public String list(Model m) {
        m.addAttribute("products", productRepo.findAll());
        return "product/list";
    }

    // 2) 등록 폼
    @GetMapping("/create-form")
    public String createForm(Model m) {
        // 빈값 세팅
        m.addAttribute("id", null);
        m.addAttribute("name", "");
        m.addAttribute("price", 0);
        m.addAttribute("description", "");
        m.addAttribute("stock", 0);
        m.addAttribute("size", "");
        m.addAttribute("color", "");
        m.addAttribute("imageUrl", "");
        return "product/create-form";
    }

    // 3) 등록 처리
    @PostMapping("/create")
    public String create(
            @RequestParam String name,
            @RequestParam int price,
            @RequestParam String description,
            @RequestParam int stock,
            @RequestParam String size,
            @RequestParam String color,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(description);
        p.setStock(stock);
        p.setSize(size);
        p.setColor(color);
        if (!imageFile.isEmpty()) {
            String fn = fileStorageService.store(imageFile);
            p.setImageUrl("/uploads/" + fn);
        }
        productRepo.save(p);
        return "redirect:/products";
    }

    // 4) 수정 폼
    @GetMapping("/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model m) {
        Product p = productRepo.findById(id).orElseThrow();
        m.addAttribute("id", p.getId());
        m.addAttribute("name", p.getName());
        m.addAttribute("price", p.getPrice());
        m.addAttribute("description", p.getDescription());
        m.addAttribute("stock", p.getStock());
        m.addAttribute("size", p.getSize());
        m.addAttribute("color", p.getColor());
        m.addAttribute("imageUrl", p.getImageUrl());
        return "product/update-form";
    }

    // 5) 수정 처리
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam int price,
            @RequestParam String description,
            @RequestParam int stock,
            @RequestParam String size,
            @RequestParam String color,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {
        Product p = productRepo.findById(id).orElseThrow();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(description);
        p.setStock(stock);
        p.setSize(size);
        p.setColor(color);
        if (!imageFile.isEmpty()) {
            String fn = fileStorageService.store(imageFile);
            p.setImageUrl("/uploads/" + fn);
        }
        productRepo.save(p);
        return "redirect:/products";
    }

    // 6) 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productRepo.deleteById(id);
        return "redirect:/products";
    }
}
