package com.prudential.interview.repository;

import com.prudential.interview.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepo extends JpaRepository<Stock, Long> {

    Stock findByCarModelId(long carModelId);
}
