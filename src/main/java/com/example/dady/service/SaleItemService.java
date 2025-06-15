package com.example.dady.service;

import com.example.dady.model.SaleItem;
import com.example.dady.repository.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;

    public List<SaleItem> getSaleItemsBySaleId(Long saleId) {
        return saleItemRepository.findBySaleId(saleId);
    }

    public List<SaleItem> getSaleItemsByProductId(Long productId) {
        return saleItemRepository.findByProductId(productId);
    }
} 