package com.example.pmapp.repository;

import com.example.pmapp.dto.ProductVarient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVarientRepository extends JpaRepository<ProductVarient, Long> {
    List<ProductVarient> findByProductId(Long productId);
}
