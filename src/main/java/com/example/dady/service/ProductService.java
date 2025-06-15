package com.example.dady.service;

import com.example.dady.model.Product;
import com.example.dady.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStockGreaterThan(0);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return getAllProducts().stream()
            .filter(p -> p.getStock() <= threshold && p.getStock() > 0)
            .collect(Collectors.toList());
    }

    public void updateProductStock(Long productId, int quantity) {
        Product product = getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getFilteredProducts(String search, BigDecimal minPrice, 
                                           BigDecimal maxPrice, String stockFilter) {
        List<Product> products = getAllProducts();

        // Apply filters
        return products.stream()
            .filter(p -> search == null || search.isEmpty() || 
                        p.getName().toLowerCase().contains(search.toLowerCase()) ||
                        (p.getDescription() != null && 
                         p.getDescription().toLowerCase().contains(search.toLowerCase())))
            .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
            .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
            .filter(p -> filterByStock(p, stockFilter))
            .collect(Collectors.toList());
    }

    private boolean filterByStock(Product product, String stockFilter) {
        if (stockFilter == null || stockFilter.isEmpty()) return true;
        
        return switch (stockFilter) {
            case "low" -> product.getStock() <= 5 && product.getStock() > 0;
            case "out" -> product.getStock() == 0;
            case "available" -> product.getStock() > 5;
            default -> true;
        };
    }
} 