package com.example.dady.controller;

import com.example.dady.model.Product;
import com.example.dady.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String stockFilter,
            Model model) {
        
        // Get filtered products
        List<Product> products = productService.getFilteredProducts(search, minPrice, maxPrice, stockFilter);
        model.addAttribute("products", products);

        // Get low stock products for alert
        List<Product> lowStockProducts = productService.getLowStockProducts(5); // threshold of 5
        if (!lowStockProducts.isEmpty()) {
            model.addAttribute("lowStockProducts", lowStockProducts);
        }

        return "products/list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("product", productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found")));
            return "products/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/products";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
} 