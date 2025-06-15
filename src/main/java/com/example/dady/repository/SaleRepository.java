package com.example.dady.repository;

import com.example.dady.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    // Find sales between date range
    List<Sale> findByDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find sales by user
    List<Sale> findByUserIdOrderByDateTimeDesc(Long userId);
    
    // Find sales with total amount greater than specified value
    List<Sale> findByTotalAmountGreaterThan(BigDecimal amount);
} 