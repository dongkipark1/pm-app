package com.example.pmapp.repository;

import com.example.pmapp.dto.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
