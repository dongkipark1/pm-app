package com.example.pmapp.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_varients", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "size", "color"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVarient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 10)
    private String size; // S, M, L

    @Column(nullable = false, length = 20)
    private String color;

    @Column(nullable = false)
    private int stockQuantity;


}
