package com.example.dady.service;

import com.example.dady.model.Sale;
import com.example.dady.model.SaleItem;
import com.example.dady.model.Product;
import com.example.dady.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private ProductService productService;

    public Sale createSale(Sale sale) {
        // Calculate total amount
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : sale.getItems()) {
            item.setSale(sale);
            item.setSubtotal(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            total = total.add(item.getSubtotal());
            
            // Update product stock
            productService.updateStock(item.getProduct().getId(), item.getQuantity());
        }
        sale.setTotalAmount(total);
        sale.setDateTime(LocalDateTime.now());
        
        return saleRepository.save(sale);
    }

    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findByDateTimeBetween(startDate, endDate);
    }

    public List<Sale> getSalesForMonth(int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return getSalesByDateRange(startDate, endDate);
    }

    public List<Sale> getSalesForDay(LocalDate date) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1).minusSeconds(1);
        return getSalesByDateRange(startDate, endDate);
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    public List<Sale> getSalesByUser(Long userId) {
        return saleRepository.findByUserIdOrderByDateTimeDesc(userId);
    }
} 