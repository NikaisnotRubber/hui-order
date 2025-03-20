package com.hui.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesDataPoint {
    private String label; // The time period (date, week, month)
    private BigDecimal revenue;
    private int orderCount;
}