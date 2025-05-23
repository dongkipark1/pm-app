package com.example.pmapp.controller;

import com.example.pmapp.dto.Stock;
import com.example.pmapp.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController {

    private final StockRepository stockRepository;

    @GetMapping
    public String listStocks(Model model){
        List<Stock> stocks = stockRepository.findAll();
        model.addAttribute("stocks", stocks);
        return "stock/list";
    }

    @GetMapping("/create-form")
    public String showCreateForm(Model model){
        model.addAttribute("stock", new Stock());
        return "stock/form";
    }

    @PostMapping("/create")
    public String createStock(@ModelAttribute Stock stock){
        stock.setTransactionDate(java.time.LocalDateTime.now());
        stockRepository.save(stock);
        return "redirect:/stocks";
    }
}
