package com.example.dady.service;

import com.example.dady.model.Sale;
import com.example.dady.model.SaleItem;
import com.example.dady.model.Product;
import com.example.dady.model.User;
import com.example.dady.repository.SaleRepository;
import com.example.dady.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserRepository userRepository;

    public List<Sale> getAllSales() {
        return saleRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTime"));
    }

    @Transactional
    public Sale createSale(Sale sale) {
        sale.setDateTime(LocalDateTime.now());
        sale.setActive(true);
        
        // Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        sale.setUser(user);

        // Calculate totals and update stock
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : sale.getItems()) {
            Product product = productService.getProductById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Validate stock
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setSale(sale);
            
            total = total.add(item.getSubtotal());
            
            // Update stock
            product.setStock(product.getStock() - item.getQuantity());
            productService.saveProduct(product);
        }
        
        sale.setTotalAmount(total);
        return saleRepository.save(sale);
    }

    @Transactional
    public void cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new RuntimeException("Sale not found"));
            
        if (!sale.isActive()) {
            throw new RuntimeException("Sale is already cancelled");
        }
        
        // Restore product stock
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.saveProduct(product);
        }
        
        sale.setActive(false);
        saleRepository.save(sale);
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