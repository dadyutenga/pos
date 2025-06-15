package com.example.dady.service;

import com.example.dady.model.Product;
import com.example.dady.model.Sale;
import com.example.dady.repository.ProductRepository;
import com.example.dady.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final int LOW_STOCK_THRESHOLD = 10; // Define threshold as constant

    public List<Map<String, Object>> getTopSellingProducts(String period, int year, int month) {
        LocalDateTime startDate = getStartDate(period, year, month);
        LocalDateTime endDate = getEndDate(period, year, month);

        List<Sale> sales = saleRepository.findByDateTimeBetween(startDate, endDate);
        
        Map<Product, Integer> productSales = new HashMap<>();
        sales.forEach(sale -> {
            sale.getItems().forEach(item -> {
                productSales.merge(item.getProduct(), item.getQuantity(), Integer::sum);
            });
        });

        return productSales.entrySet().stream()
            .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
            .limit(5)
            .map(entry -> {
                Map<String, Object> product = new HashMap<>();
                product.put("name", entry.getKey().getName());
                product.put("quantity", entry.getValue());
                product.put("revenue", entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
                return product;
            })
            .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts() {
        try {
            List<Product> lowStockProducts = productRepository.findByStockLessThanOrderByStockAsc(LOW_STOCK_THRESHOLD);
            return lowStockProducts != null ? lowStockProducts : new ArrayList<>();
        } catch (Exception e) {
            // Log the error if you have a logger configured
            // logger.error("Error fetching low stock products", e);
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getSalesStatistics(String period, int year, int month) {
        LocalDateTime startDate = getStartDate(period, year, month);
        LocalDateTime endDate = getEndDate(period, year, month);

        List<Sale> sales = saleRepository.findByDateTimeBetween(startDate, endDate);
        
        BigDecimal totalRevenue = sales.stream()
            .map(Sale::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageOrderValue = sales.isEmpty() ? BigDecimal.ZERO :
            totalRevenue.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP);

        int totalItems = sales.stream()
            .flatMap(sale -> sale.getItems().stream())
            .mapToInt(item -> item.getQuantity())
            .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", sales.size());
        stats.put("totalRevenue", totalRevenue);
        stats.put("averageOrderValue", averageOrderValue);
        stats.put("totalItems", totalItems);

        return stats;
    }

    public List<Map<String, Object>> getMonthlySalesData(int year) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            
            List<Sale> sales = saleRepository.findByDateTimeBetween(startDate, endDate);
            BigDecimal revenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("sales", sales.size());
            monthData.put("revenue", revenue);
            
            monthlyData.add(monthData);
        }
        
        return monthlyData;
    }

    private LocalDateTime getStartDate(String period, int year, int month) {
        return switch (period) {
            case "year" -> LocalDateTime.of(year, 1, 1, 0, 0);
            case "month" -> LocalDateTime.of(year, month, 1, 0, 0);
            default -> LocalDateTime.of(year, month, 1, 0, 0);
        };
    }

    private LocalDateTime getEndDate(String period, int year, int month) {
        return switch (period) {
            case "year" -> LocalDateTime.of(year, 12, 31, 23, 59, 59);
            case "month" -> LocalDateTime.of(year, month, 1, 23, 59, 59)
                .plusMonths(1).minusSeconds(1);
            default -> LocalDateTime.of(year, month, 1, 23, 59, 59)
                .plusMonths(1).minusSeconds(1);
        };
    }
} 