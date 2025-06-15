package com.example.dady.controller;

import com.example.dady.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public String showReports(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        // Default to current month if no period specified
        if (period == null) {
            period = "month";
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        // Add report data to model
        model.addAttribute("topProducts", reportService.getTopSellingProducts(period, year, month));
        model.addAttribute("lowStockProducts", reportService.getLowStockProducts());
        model.addAttribute("salesStats", reportService.getSalesStatistics(period, year, month));
        model.addAttribute("monthlySales", reportService.getMonthlySalesData(year));
        model.addAttribute("selectedPeriod", period);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);

        return "reports/dashboard";
    }
} 