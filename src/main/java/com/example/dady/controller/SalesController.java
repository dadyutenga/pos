package com.example.dady.controller;

import com.example.dady.model.Sale;
import com.example.dady.model.SaleItem;
import com.example.dady.model.Product;
import com.example.dady.service.SaleService;
import com.example.dady.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showSalesPage(Model model) {
        model.addAttribute("products", productService.getAvailableProducts());
        return "sales/create";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createSale(@RequestBody Sale sale) {
        try {
            Sale savedSale = saleService.createSale(sale);
            return ResponseEntity.ok(savedSale);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/receipt/{id}")
    public String showReceipt(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Sale sale = saleService.getSaleById(id);
            model.addAttribute("sale", sale);
            return "sales/receipt";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Receipt not found");
            return "redirect:/sales";
        }
    }

    @GetMapping("/product/{id}")
    @ResponseBody
    public ResponseEntity<?> getProductDetails(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public String showSalesHistory(Model model) {
        model.addAttribute("sales", saleService.getAllSales());
        return "sales/history";
    }
} 