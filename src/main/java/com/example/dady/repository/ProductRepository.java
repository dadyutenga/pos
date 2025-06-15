package com.example.dady.repository;

import com.example.dady.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find products by name containing the search term (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find products with stock less than specified amount
    List<Product> findByStockLessThan(Integer stockThreshold);

    List<Product> findByStockGreaterThan(int stock);
} 