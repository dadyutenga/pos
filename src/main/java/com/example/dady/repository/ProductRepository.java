package com.example.dady.repository;

import com.example.dady.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find products with stock less than given amount, ordered by stock ascending
    List<Product> findByStockLessThanOrderByStockAsc(Integer threshold);
    
    // Find products with stock greater than zero
    List<Product> findByStockGreaterThan(Integer stock);
    
    // Find by name containing (for search)
    List<Product> findByNameContainingIgnoreCase(String name);
} 